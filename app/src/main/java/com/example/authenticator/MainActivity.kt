package com.example.authenticator

import androidx.lifecycle.asLiveData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.authenticator.data.AppDatabase
import com.example.authenticator.databinding.ActivityMainBinding
import com.example.authenticator.viewmodel.AccountViewModel
import com.example.authenticator.viewmodel.AccountViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var accountAdapter: AccountAdapter
    private val viewModel: AccountViewModel by viewModels {
        AccountViewModelFactory(AppDatabase.getDatabase(this).accountDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeAccounts()
        setupButtons()
    }


    private fun setupRecyclerView() {
        accountAdapter = AccountAdapter(emptyList())
        binding.rvAccounts.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = accountAdapter
        }
    }

    private fun observeAccounts() {
        viewModel.allAccounts.observe(this) { accounts ->
            accountAdapter.updateAccounts(accounts)
        }
    }

    private fun setupButtons() {
        binding.btnAddManual.setOnClickListener {
            startActivity(Intent(this, activity_manual_add::class.java))
        }
        binding.btnScanQr.setOnClickListener {
            startActivity(Intent(this, QrScannerActivity::class.java))
        }

    }


}