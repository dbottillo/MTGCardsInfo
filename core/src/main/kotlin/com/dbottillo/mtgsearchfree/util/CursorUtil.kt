package com.dbottillo.mtgsearchfree.util

import android.content.ContentValues
import android.database.Cursor
import org.json.JSONObject

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