package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.resources.CardsBucket
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.view.CardsView
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CardsPresenterImpl(var interactor: CardsInteractor) : CardsPresenter {

    override fun saveAsFavourite(card: MTGCard) {
        interactor.saveAsFavourite(card)
    }

    var cardsView: CardsView? = null

    override fun loadCards(set: MTGSet) {
        var currentBucket = CardsMemoryStorage.bucket
        if (currentBucket != null && currentBucket.isValid(set.name)) {
            cardsView?.cardLoaded(currentBucket)
            return;
        }
        var obs = interactor.load(set)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        obs.subscribe {
            CardsMemoryStorage.bucket = CardsBucket(set, it)
            cardsView?.cardLoaded(CardsMemoryStorage.bucket!!)
        }
    }

    override fun init(view: CardsView) {
        cardsView = view
    }

}