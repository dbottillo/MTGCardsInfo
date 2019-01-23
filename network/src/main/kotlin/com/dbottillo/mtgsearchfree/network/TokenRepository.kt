package com.dbottillo.mtgsearchfree.network

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class TokenRepository @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences by lazy(LazyThreadSafetyMode.NONE) { context.getSharedPreferences("Token", Context.MODE_PRIVATE) }

    fun get(): String? {
        return sharedPreferences.getString("TOKEN", null)
    }

    fun save(token: String) {
        sharedPreferences.edit().putString("TOKEN", token).apply()
    }
}