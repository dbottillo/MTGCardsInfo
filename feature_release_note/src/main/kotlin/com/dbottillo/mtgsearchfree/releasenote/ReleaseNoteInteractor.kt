package com.dbottillo.mtgsearchfree.releasenote

import com.dbottillo.mtgsearchfree.interactor.SchedulerProvider
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.Single
import javax.inject.Inject

class ReleaseNoteInteractor @Inject constructor(
    private val repo: ReleaseNoteStorage,
    private val schedulerProvider: SchedulerProvider,
    private val logger: Logger
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
