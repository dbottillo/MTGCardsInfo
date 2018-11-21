package com.dbottillo.mtgsearchfree.model.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.util.LOG
import org.json.JSONException
import org.json.JSONObject

class SetDataSource(private val database: SQLiteDatabase) {

    val sets: List<MTGSet>
        get() {
            val query = "SELECT * FROM $TABLE"
            val cursor = database.rawQuery(query, null)
            val sets = ArrayList<MTGSet>()
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    sets.add(fromCursor(cursor))
                    cursor.moveToNext()
                }
            }
            LOG.query(query)
            cursor.close()
            return sets
        }

    fun saveSet(set: MTGSet): Long {
        val values = ContentValues().apply {
            if (set.id > -1) {
                put("_id", set.id)
            }
            put("name", set.name)
            put("code", set.code)
        }
        return database.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun removeSet(id: Long) {
        val args = arrayOf(id.toString() + "")
        val query = "DELETE FROM $TABLE where _id=? "
        val cursor = database.rawQuery(query, args)
        cursor.moveToFirst()
        cursor.close()
        LOG.query(query, *args)
    }

    fun fromCursor(cursor: Cursor): MTGSet {
        return MTGSet(id = cursor.getIntFromColumn("_id"),
                name = cursor.getStringFromColumn("name"),
                code = cursor.getStringFromColumn("code"))
    }

    @Throws(JSONException::class)
    fun fromJSON(jsonObject: JSONObject): ContentValues {
        return ContentValues().apply {
            fromJson("name", jsonObject)
            fromJson("code", jsonObject)
        }
    }

    companion object {

        const val TABLE = "MTGSet"

        fun generateCreateTable(): String {
            val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ")
            builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ")
            builder.add(name = "name", type = "TEXT")
            builder.add(name = "code", type = "TEXT", last = true)
            return builder.append(')').toString()
        }
    }
}

fun ContentValues.fromJson(name: String, jsonObject: JSONObject) {
    put(name, jsonObject.getString(name))
}

fun StringBuilder.add(name: String, type: String, last: Boolean = false) {
    append(name).append(' ').append(type)
    if (!last) {
        append(",")
    }
}

fun Cursor.getIntFromColumn(column: String): Int {
    return getInt(getColumnIndex(column))
}

fun Cursor.getStringFromColumn(column: String): String {
    return getString(getColumnIndex(column))
}