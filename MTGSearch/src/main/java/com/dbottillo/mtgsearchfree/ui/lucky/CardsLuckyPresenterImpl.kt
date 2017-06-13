package com.dbottillo.mtgsearchfree.ui.lucky

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.util.ArrayUtils
import com.dbottillo.mtgsearchfree.util.Logger

class CardsLuckyPresenterImpl(val cardsInteractor: CardsInteractor,
                              val cardsPreferences: CardsPreferences,
                              val factory: RunnerFactory,
                              val logger: Logger) : CardsLuckyPresenter{

    lateinit var view: CardsLuckyView

    internal var luckyCards = mutableListOf<MTGCard>()
    internal var currentCard: MTGCard? = null
    internal lateinit var favs: MutableList<Int>

    val idFavsRunner: Runner<IntArray> = factory.simple()
    val runnerCards: Runner<CardsCollection> = factory.simple()

    override fun init(view: CardsLuckyView, bundle: Bundle?, intent: Intent?) {
        this.view = view

        bundle?.let{
            luckyCards.addAll(bundle.getParcelableArrayList(CARDS))
            currentCard = bundle.getParcelable<MTGCard>(CARD)
            loadCurrentCard()
        }

        idFavsRunner.run(cardsInteractor.loadIdFav(), object : Runner.RxWrapperListener<IntArray> {
            override fun onNext(data: IntArray) {
                favs = data.toMutableList()
            }

            override fun onError(e: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCompleted() {
                if (intent != null && intent.hasExtra(CARD)) {
                    currentCard = intent.getParcelableExtra<Parcelable>(CARD) as MTGCard
                    loadCurrentCard()
                    return
                }
                loadMoreCards()
            }
        })
    }

    private fun loadMoreCards() {
        runnerCards.run(cardsInteractor.getLuckyCards(LUCKY_BATCH_CARDS), object : Runner.RxWrapperListener<CardsCollection>{
            override fun onNext(data: CardsCollection) {
                luckyCards.addAll(data.list)
                data.list.forEach {
                    view.preFetchCardImage(it)
                }
            }

            override fun onError(e: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCompleted() {
                if (currentCard == null){
                    showNextCard()
                }
            }
        })
    }

    override fun showNextCard(){
        if (luckyCards.isEmpty()){
            currentCard = null
            loadMoreCards()
            return
        }
        currentCard = luckyCards.removeAt(0)
        loadCurrentCard()

        if (luckyCards.size <= 2) {
            loadMoreCards()
        }
    }

    internal fun loadCurrentCard(){
        currentCard?.let {
            view.showCard(it, cardsPreferences.showImage())
            updateMenu()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(CARDS, ArrayList(luckyCards))
        outState.putParcelable(CARD, currentCard)
    }

    override fun updateMenu() {
        logger.d()
        if (favs.isEmpty()) {
            // too early
            return
        }
        if (currentCard != null && currentCard!!.multiVerseId > 0) {
            view.showFavMenuItem()
            if (favs.contains(currentCard?.multiVerseId)) {
                view.updateFavMenuItem(R.string.favourite_remove, R.drawable.ab_star_colored)
            } else {
                view.updateFavMenuItem(R.string.favourite_add, R.drawable.ab_star)
            }
        } else {
            view.hideFavMenuItem()
        }
        if (cardsPreferences.showImage()) {
            view.setImageMenuItemChecked(true)
        } else {
            view.setImageMenuItemChecked(false)
        }
    }

    override fun saveOrRemoveCard() {
        currentCard?.let {
            if (favs.contains(it.multiVerseId)) {
                cardsInteractor.removeFromFavourite(it)
                favs.remove(it.multiVerseId)
            } else {
                cardsInteractor.saveAsFavourite(it)
                favs.add(it.multiVerseId)
            }
            updateMenu()
        }
    }

    companion object {
        val CARD = "CARD"
        val CARDS = "luckyCards"
        val LUCKY_BATCH_CARDS = 10
    }
}