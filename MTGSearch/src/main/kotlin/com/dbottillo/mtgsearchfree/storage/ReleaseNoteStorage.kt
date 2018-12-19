package com.dbottillo.mtgsearchfree.storage

import android.content.res.Resources
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.about.ReleaseNoteItem
import com.dbottillo.mtgsearchfree.util.FileManager
import com.dbottillo.mtgsearchfree.util.GsonUtil
import com.google.gson.JsonSyntaxException
import io.reactivex.Single
import javax.inject.Inject

class ReleaseNoteStorage @Inject constructor(
    private val fileManager: FileManager,
    private val gsonUtil: GsonUtil
) {

    fun load(): Single<List<ReleaseNoteItem>> {
        return try {
            val input = fileManager.loadRaw(R.raw.release_note)
            Single.just(gsonUtil.toListReleaseNote(input))
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
