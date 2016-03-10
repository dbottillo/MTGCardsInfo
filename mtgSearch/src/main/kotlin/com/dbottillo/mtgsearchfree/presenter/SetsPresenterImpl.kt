package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.view.SetsView
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SetsPresenterImpl(var interactor: SetsInteractor) : SetsPresenter {

    var setView: SetsView? = null

    override fun init(view: SetsView) {
        setView = view
    }

    override fun loadSets() {
        if (SetsMemoryStorage.init) {
            setView?.setsLoaded(SetsMemoryStorage.sets)
            return;
        }
        var obs = interactor.load()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe {
            SetsMemoryStorage.init = true
            SetsMemoryStorage.sets = it
            setView?.setsLoaded(it)
        }
    }

}