package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.storage.SetDataSource
import com.dbottillo.mtgsearchfree.interactor.SchedulerProvider
import com.dbottillo.mtgsearchfree.util.Logger

import io.reactivex.Observable

class SetsInteractorImpl(
    private val storage: SetDataSource,
    private val schedulerProvider: SchedulerProvider,
    private val logger: Logger
) : SetsInteractor {

    init {
        logger.d("created")
    }

    override fun load(): Observable<List<MTGSet>> {
        logger.d("loadSet sets")
        return Observable.fromCallable { storage.sets }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }
}
