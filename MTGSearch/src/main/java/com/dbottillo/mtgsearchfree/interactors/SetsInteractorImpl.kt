package com.dbottillo.mtgsearchfree.interactors

import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.database.SetDataSource
import com.dbottillo.mtgsearchfree.util.Logger

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SetsInteractorImpl(private val storage: SetDataSource,
                         private val schedulerProvider: SchedulerProvider,
                         private val logger: Logger) : SetsInteractor {

    init {
        logger.d("created")
    }

    override fun load(): Observable<List<MTGSet>> {
        logger.d("loadSet sets")
        return Observable.just(storage.sets)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

}
