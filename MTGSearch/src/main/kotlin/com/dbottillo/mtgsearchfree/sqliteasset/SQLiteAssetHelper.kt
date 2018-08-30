/*
 * Copyright (C) 2011 readyState Software Ltd, 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dbottillo.mtgsearchfree.sqliteasset

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.*

open class SQLiteAssetHelper(private val context: Context,
                             private val name: String,
                             private val version: Int) : SQLiteOpenHelper(context, name, null, version) {

    private var database: SQLiteDatabase? = null
    private var isInitializing = false
    private var databasePath = context.applicationInfo.dataDir + "/databases"
    private val mAssetPath = "$ASSET_DB_PATH/$name"

    init {
        if (version < 1) throw IllegalArgumentException("Version must be >= 1, was $version")
    }

    @Synchronized
    override fun getWritableDatabase(): SQLiteDatabase {
        throw UnsupportedOperationException("this database is read only")
    }

    @Synchronized
    override fun getReadableDatabase(): SQLiteDatabase {
        val currentDb = database
        if (currentDb != null && currentDb.isOpen) {
            return currentDb // The database is already open for business
        }

        if (isInitializing) {
            throw IllegalStateException("getReadableDatabase called recursively")
        }

        isInitializing = true
        var db = returnDatabase()

        if (db == null || db.version < version) {
            Log.e(TAG, if (db == null) "database is null" else "database version ${db.version} is lower than ${version}")
            Log.e(TAG, "will try to copy from asset")
            db?.close()
            copyDatabaseFromAssets()

            db = returnDatabase()

            if (db == null) {
                throw SQLiteAssetException("the database is null after copying from asset")
            }

            db.version = version
            onOpen(db)
            database = db
        } else {
            database = db
        }
        isInitializing = false
        return database!!
    }

    @Synchronized
    override fun close() {
        if (isInitializing) throw SQLiteAssetException("Closed during initialization")

        if (database != null && database?.isOpen == true) {
            database?.close()
            database = null
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        // not supported!
    }

    override fun onCreate(db: SQLiteDatabase) {
        // do nothing - createOrOpenDatabase() is called in
        // getWritableDatabase() to handle database creation.
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // not supported!
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // not supported!
    }

    private fun returnDatabase(): SQLiteDatabase? {
        return try {
            Log.i(TAG, "successfully opened database $name");
            SQLiteDatabase.openDatabase("$databasePath/$name", null, SQLiteDatabase.OPEN_READWRITE)
        } catch (e: SQLiteException) {
            Log.w(TAG, "could not open database " + name + " - " + e.message)
            null
        }

    }

    @Throws(SQLiteAssetException::class)
    private fun copyDatabaseFromAssets() {
        Log.w(TAG, "copying database from assets...")

        val path = mAssetPath
        val dest = "$databasePath/$name"
        val inputStream: InputStream

        try {
            // try uncompressed
            inputStream = context.assets.open(path)
        } catch (e: IOException) {
            throw SQLiteAssetException("Missing $mAssetPath file (or .zip, .gz archive) in assets, or target folder not writable")
        }

        try {
            val f = File("$databasePath/")
            if (!f.exists()) {
                f.mkdir()
            }
            inputStream.writeExtractedFileToDisk(FileOutputStream(dest))
            Log.w(TAG, "database copy complete")

        } catch (e: IOException) {
            val se = SQLiteAssetException("Unable to write $dest to data directory")
            se.stackTrace = e.stackTrace
            throw se
        }

    }

    companion object {
        private val TAG = SQLiteAssetHelper::class.java.simpleName
        private const val ASSET_DB_PATH = "databases"
    }

}

class SQLiteAssetException(error: String) : SQLiteException(error)

@Throws(IOException::class)
fun InputStream.writeExtractedFileToDisk(outs: OutputStream) {
    val buffer = ByteArray(1024)
    var length: Int = read(buffer)
    while (length > 0) {
        outs.write(buffer, 0, length)
        length = read(buffer)
    }
    outs.flush()
    outs.close()
    close()
}
