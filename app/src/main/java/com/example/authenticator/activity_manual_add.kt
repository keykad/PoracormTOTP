package com.example.authenticator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.authenticator.data.Account
import com.example.authenticator.data.AppDatabase
import com.example.authenticator.databinding.ActivityManualAddBinding
import com.example.authenticator.viewmodel.AccountViewModel
import com.example.authenticator.viewmodel.AccountViewModelFactory
import kotlinx.coroutines.launch

class activity_manual_add : AppCompatActivity() {
    private lateinit var binding: ActivityManualAddBinding
    private lateinit var viewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация ViewModel
        val dao = AppDatabase.getDatabase(this).accountDao()
        viewModel = ViewModelProvider(this, AccountViewModelFactory(dao)).get(AccountViewModel::class.java)

        // Обработчик кнопки "Сохранить"
        binding.btnSave.setOnClickListener {
            val issuer = binding.etIssuer.text.toString()
            val username = binding.etUsername.text.toString()
            val secret = binding.etSecret.text.toString()

            if (issuer.isNotEmpty() && secret.isNotEmpty()) {
                // Запуск корутины
                lifecycleScope.launch {
                    val account = Account(
                        issuer = issuer,
                        username = username,
                        secret = secret
                    )
                    viewModel.insert(account)
                    finish()
                }
            } else {
                Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show()
            }
        }
    }
}