package com.dbottillo.mtgsearchfree.storage

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SetType
import com.dbottillo.mtgsearchfree.model.SetType.COMMANDER
import com.dbottillo.mtgsearchfree.model.SetType.EXPANSION
import com.dbottillo.mtgsearchfree.model.SetType.FUNNY
import com.dbottillo.mtgsearchfree.model.SetType.PREVIEW
import com.dbottillo.mtgsearchfree.model.SetType.PROMO
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.add
import com.dbottillo.mtgsearchfree.util.fromJson
import com.dbottillo.mtgsearchfree.util.getIntFromColumn
import com.dbottillo.mtgsearchfree.util.getStringFromColumn
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
            put("type", wrap(set.type))
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
        val type = cursor.getString(cursor.getColumnIndex("type")) ?: null
        return MTGSet(id = cursor.getIntFromColumn("_id"),
                name = cursor.getStringFromColumn("name"),
                code = cursor.getStringFromColumn("code"),
                type = unwrap(type))
    }

    fun unwrap(input: String?): SetType {
        return when (input) {
            "preview" -> PREVIEW
            "funny" -> FUNNY
            "commander" -> COMMANDER
            "promo" -> PROMO
            else -> EXPANSION
        }
    }

    private fun wrap(input: SetType): String {
        return when (input) {
            PREVIEW -> "preview"
            FUNNY -> "funny"
            else -> "normal"
        }
    }

    @Throws(JSONException::class)
    fun fromJSON(jsonObject: JSONObject): ContentValues {
        return ContentValues().apply {
            fromJson("name", jsonObject)
            fromJson("code", jsonObject)
            fromJson("type", jsonObject)
        }
    }

    companion object {

        const val TABLE = "MTGSet"

        fun generateCreateTable(): String {
            val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ")
            builder.append(TABLE).append(" (_id INTEGER PRIMARY KEY, ")
            builder.add(name = "name", type = "TEXT")
            builder.add(name = "code", type = "TEXT")
            builder.add(name = "type", type = "TEXT", last = true)
            return builder.append(')').toString()
        }
    }
}