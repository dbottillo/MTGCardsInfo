package com.dbottillo.mtgsearchfree.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.dbottillo.mtgsearchfree.model.storage.database.CardDataSource
import com.dbottillo.mtgsearchfree.model.storage.database.SetDataSource

/**
 * Helper for create the database from the json in debug mode
 */
class CreateDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SetDataSource.generateCreateTable())
        db.execSQL(CardDataSource.generateCreateTable())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.delete(SetDataSource.TABLE, null, null)
        db.delete(CardDataSource.TABLE, null, null)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "MTGCardsInfo.db"
    }
}
