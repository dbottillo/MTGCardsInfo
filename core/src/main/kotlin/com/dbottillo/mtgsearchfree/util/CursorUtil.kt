package com.dbottillo.mtgsearchfree.util

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import org.json.JSONObject

fun ContentValues.fromJson(name: String, jsonObject: JSONObject) {
    put(name, if (jsonObject.has(name)) jsonObject.getString(name) else null)
}

fun StringBuilder.add(name: String, type: String, last: Boolean = false) {
    append(name).append(' ').append(type)
    if (!last) {
        append(",")
    }
}

@SuppressLint("Range")
fun Cursor.getIntFromColumn(column: String): Int {
    return getInt(getColumnIndex(column))
}

@SuppressLint("Range")
fun Cursor.getStringFromColumn(column: String): String {
    return getString(getColumnIndex(column))
}