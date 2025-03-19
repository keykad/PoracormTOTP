package com.example.authenticator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.ViewModel
import com.example.authenticator.data.Account
import com.example.authenticator.AccountDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountViewModel(private val accountDao: AccountDao) : ViewModel() {
    val allAccounts: LiveData<List<Account>> = accountDao.getAll().asLiveData()

    suspend fun insert(account: Account) {
        withContext(Dispatchers.IO) {
            accountDao.insert(account)
        }
    }

    suspend fun delete(account: Account) {
        withContext(Dispatchers.IO) {
            accountDao.delete(account)
        }
    }
}