package com.example.authenticator

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.authenticator.data.Account
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts")
    fun getAll(): Flow<List<Account>>

    @Insert
    suspend fun insert(account: Account)

    @Delete
    suspend fun delete(account: Account)
}