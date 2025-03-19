package com.example.authenticator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.authenticator.data.Account
import com.example.authenticator.data.AppDatabase
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QrScannerActivity : AppCompatActivity() {
    private lateinit var barcodeView: DecoratedBarcodeView
    private val CAMERA_PERMISSION_REQUEST = 101
    private val TAG = "QR_SCANNER"

    // Callback для обработки результатов сканирования
    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            handleScannedUri(result.text)
            finish()
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {
            // Опционально: обработка промежуточных результатов
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scanner)

        // Проверка разрешения камеры
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            initScanner()
        }
    }

    private fun initScanner() {
        barcodeView = findViewById(R.id.barcode_view)
        barcodeView.decodeContinuous(callback)
    }

    private fun handleScannedUri(uri: String) {
        try {
            val parsedUri = Uri.parse(uri)
            if (parsedUri.scheme != "otpauth" || parsedUri.host != "totp") {
                Toast.makeText(this, "Неверный QR-код", Toast.LENGTH_SHORT).show()
                return
            }

            val path = parsedUri.path?.substring(1) ?: ""
            val secret = parsedUri.getQueryParameter("secret") ?: ""
            val issuer = parsedUri.getQueryParameter("issuer") ?: "Unknown"

            val username = path.split(":").getOrNull(1) ?: "Unknown"

            // Сохранение в базу данных
            CoroutineScope(Dispatchers.IO).launch {
                val account = Account(
                    issuer = issuer,
                    username = username,
                    secret = secret
                )
                AppDatabase.getDatabase(this@QrScannerActivity)
                    .accountDao()
                    .insert(account)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка обработки QR-кода", e)
            Toast.makeText(this, "Ошибка сканирования", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner()
            } else {
                Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::barcodeView.isInitialized) {
            barcodeView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::barcodeView.isInitialized) {
            barcodeView.pause()
        }
    }
}