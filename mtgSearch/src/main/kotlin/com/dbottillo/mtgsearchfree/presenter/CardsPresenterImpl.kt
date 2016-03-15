package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.resources.CardsBucket
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.view.CardsView
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CardsPresenterImpl(var interactor: CardsInteractor) : CardsPresenter {

    var subscription: Subscription? = null

    override fun getLuckyCards(howMany: Int) {
        var obs = interactor.getLuckyCards(howMany)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe {
            cardsView?.luckyCardsLoaded(it)
        }
    }

    override fun removeFromFavourite(card: MTGCard) {
        var obs = interactor.removeFromFavourite(card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe {
            CardsMemoryStorage.favourites = it
            cardsView?.favIdLoaded(it)
        }
    }

    override fun saveAsFavourite(card: MTGCard) {
        var obs = interactor.saveAsFavourite(card)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe {
            CardsMemoryStorage.favourites = it
            cardsView?.favIdLoaded(it)
        }
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
        subscription = obs.subscribe {
            CardsMemoryStorage.bucket = CardsBucket(set, it)
            cardsView?.cardLoaded(CardsMemoryStorage.bucket!!)
        }
    }

    override fun init(view: CardsView) {
        cardsView = view
    }

    override fun loadIdFavourites() {
        var currentFav = CardsMemoryStorage.favourites
        if (currentFav != null) {
            cardsView?.favIdLoaded(currentFav)
            return;
        }
        var obs = interactor.loadIdFav()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
        subscription = obs.subscribe {
            CardsMemoryStorage.favourites = it
            cardsView?.favIdLoaded(it)
        }
    }

    override fun detachView() {
        subscription?.unsubscribe()
    }

}