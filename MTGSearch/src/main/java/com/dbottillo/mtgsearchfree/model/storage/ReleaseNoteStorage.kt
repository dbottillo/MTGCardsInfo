package com.dbottillo.mtgsearchfree.model.storage

import android.content.res.Resources
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.about.ReleaseNoteItem
import com.dbottillo.mtgsearchfree.util.FileLoader
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import javax.inject.Inject

class ReleaseNoteStorage @Inject constructor(private val fileLoader: FileLoader, private val gson: Gson) {

    fun load(): Single<List<ReleaseNoteItem>> {
        return try {
            val input = fileLoader.loadRaw(R.raw.release_note)
            Single.just(input.fromJson<List<ReleaseNoteItem>>(gson))
        } catch (e: Exception) {
            val throwable = when (e) {
                is Resources.NotFoundException -> Throwable("impossible to load release note raw file")
                is JsonSyntaxException -> Throwable("impossible to read json")
                else -> Throwable(e.localizedMessage)
            }
            Single.error(throwable)
        }
    }

}

fun <T> String.fromJson(gson: Gson): T {
    return gson.fromJson(this, object : TypeToken<T>() {}.type)
}