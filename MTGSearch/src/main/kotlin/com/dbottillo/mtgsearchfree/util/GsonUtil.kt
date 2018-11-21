package com.dbottillo.mtgsearchfree.util

import com.dbottillo.mtgsearchfree.ui.about.ReleaseNoteItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GsonUtil(val gson: Gson) {

    fun toListReleaseNote(input: String): List<ReleaseNoteItem> {
        return gson.fromJson(input, object : TypeToken<List<ReleaseNoteItem>>() {}.type)
    }
}