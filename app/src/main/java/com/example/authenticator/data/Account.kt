package com.example.authenticator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val issuer: String,
    val username: String,
    val secret: String, //  (Base32)
    val createdAt: Long = System.currentTimeMillis()
)