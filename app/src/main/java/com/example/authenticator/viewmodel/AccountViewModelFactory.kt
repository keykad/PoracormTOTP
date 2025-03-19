package com.example.authenticator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.authenticator.AccountDao

class AccountViewModelFactory(private val accountDao: AccountDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(accountDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}