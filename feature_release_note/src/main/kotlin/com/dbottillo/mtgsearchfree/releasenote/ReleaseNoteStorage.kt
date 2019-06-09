package com.dbottillo.mtgsearchfree.releasenote

import android.content.res.Resources
import com.dbottillo.mtgsearchfree.util.FileManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import javax.inject.Inject

class ReleaseNoteStorage @Inject constructor(
    private val fileManager: FileManager,
    private val gson: Gson
) {

    fun load(): Single<List<ReleaseNoteItem>> {
        return try {
            val input = fileManager.loadRaw(R.raw.release_note)
            Single.just(toListReleaseNote(input))
        } catch (e: Exception) {
            val throwable = when (e) {
                is Resources.NotFoundException -> Throwable("impossible to load release note raw file")
                is JsonSyntaxException -> Throwable("impossible to read json")
                else -> Throwable(e.localizedMessage)
            }
            Single.error(throwable)
        }
    }

    private fun toListReleaseNote(input: String): List<ReleaseNoteItem> {
        return gson.fromJson(input, object : TypeToken<List<ReleaseNoteItem>>() {}.type)
    }
}