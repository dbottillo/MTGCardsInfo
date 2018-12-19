package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.storage.ReleaseNoteStorage
import com.dbottillo.mtgsearchfree.ui.about.ReleaseNoteItem
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Single
import javax.inject.Inject

class ReleaseNoteInteractor @Inject constructor(
    val repo: ReleaseNoteStorage,
    val schedulerProvider: SchedulerProvider,
    val logger: Logger
) {

    init {
        logger.d("created")
    }

    fun load(): Single<List<ReleaseNoteItem>> {
        logger.d("load release note")
        return repo.load()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }
}
