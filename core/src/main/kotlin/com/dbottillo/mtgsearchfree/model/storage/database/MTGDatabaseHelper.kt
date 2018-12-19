package com.dbottillo.mtgsearchfree.model.database

import android.content.Context

import com.dbottillo.mtgsearchfree.core.BuildConfig
import com.dbottillo.mtgsearchfree.sqliteasset.SQLiteAssetHelper

/**
 * Helper for access the card database, only on read mode
 * the database is created from [CreateDatabaseHelper]
 * and then copied to database folder from the library [SQLiteAssetHelper]
 */
class MTGDatabaseHelper(context: Context) : SQLiteAssetHelper(context, "mtgsearch.db", BuildConfig.DATABASE_VERSION)
