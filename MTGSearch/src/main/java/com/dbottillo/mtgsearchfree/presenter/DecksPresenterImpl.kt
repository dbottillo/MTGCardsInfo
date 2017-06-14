package com.dbottillo.mtgsearchfree.presenter

import android.net.Uri

import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.mapper.DeckMapper
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckBucket
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.view.DecksView

import javax.inject.Inject

import io.reactivex.functions.Function

class DecksPresenterImpl @Inject
constructor(private val interactor: DecksInteractor, private val deckMapper: DeckMapper,
            runnerFactory: RunnerFactory, private val logger: Logger) : DecksPresenter {
    lateinit var decksView: DecksView
    private val exportWrapper: Runner<Boolean> = runnerFactory.simple<Boolean>()
    private val deckWrapper: Runner<List<Deck>> = runnerFactory.simple<List<Deck>>()
    private val cardWrapper: RunnerAndMap<CardsCollection, DeckBucket> = runnerFactory.withMap<CardsCollection, DeckBucket>()

    init {
        logger.d("created")
    }

    override fun init(view: DecksView) {
        logger.d()
        decksView = view
    }

    override fun loadDecks() {
        logger.d()
        deckWrapper.run(interactor.load(), deckObserver)
    }

    override fun loadDeck(deck: Deck) {
        logger.d("loadSet " + deck)
        //cardWrapper.runAndMap(interactor.loadDeck(deck), mapper, cardsObserver)
    }

    override fun addDeck(name: String) {
        logger.d("add " + name)
        deckWrapper.run(interactor.addDeck(name), deckObserver)
    }

    override fun deleteDeck(deck: Deck) {
        logger.d("delete " + deck)
        deckWrapper.run(interactor.deleteDeck(deck), deckObserver)
    }

    override fun editDeck(deck: Deck, name: String) {
        logger.d("edit $deck with $name")
        //  cardWrapper.runAndMap(interactor.editDeck(deck, name), mapper, cardsObserver)
    }

    override fun addCardToDeck(name: String, card: MTGCard, quantity: Int) {
        logger.d()
        // cardWrapper.runAndMap(interactor.addCard(name, card, quantity), mapper, cardsObserver)
    }

    override fun addCardToDeck(deck: Deck, card: MTGCard, quantity: Int) {
        logger.d()
        // cardWrapper.runAndMap(interactor.addCard(deck, card, quantity), mapper, cardsObserver)
    }

    override fun removeCardFromDeck(deck: Deck, card: MTGCard) {
        logger.d()
        //  cardWrapper.runAndMap(interactor.removeCard(deck, card), mapper, cardsObserver)
    }

    override fun removeAllCardFromDeck(deck: Deck, card: MTGCard) {
        logger.d()
        // cardWrapper.runAndMap(interactor.removeAllCard(deck, card), mapper, cardsObserver)
    }

    override fun moveCardFromSideBoard(deck: Deck, card: MTGCard, quantity: Int) {
        logger.d()
        // cardWrapper.runAndMap(interactor.moveCardFromSideboard(deck, card, quantity), mapper, cardsObserver)
    }

    override fun moveCardToSideBoard(deck: Deck, card: MTGCard, quantity: Int) {
        logger.d()
        //  cardWrapper.runAndMap(interactor.moveCardToSideboard(deck, card, quantity), mapper, cardsObserver)
    }

    override fun importDeck(uri: Uri) {
        logger.d("import " + uri.toString())
        // deckWrapper.run(interactor.importDeck(uri), deckObserver)
    }

    override fun exportDeck(deck: Deck, cards: CardsCollection) {
      /*  exportWrapper.run(interactor.exportDeck(deck, cards), object : Runner.RxWrapperListener<Boolean> {
            override fun onNext(data: Boolean) {
                decksView.deckExported(data)
            }

            override fun onError(e: Throwable) {

            }

            override fun onCompleted() {

            }
        })*/
    }

    //private val mapper = Function<CardsCollection, DeckBucket> { mtgCards -> deckMapper.map(mtgCards) }

    private val deckObserver = object : Runner.RxWrapperListener<List<Deck>> {
        override fun onNext(decks: List<Deck>) {
            logger.d()
            decksView.decksLoaded(decks)
        }

        override fun onError(e: Throwable) {
            if (e is MTGException) {
                decksView.showError(e)
            } else {
                decksView.showError(e.localizedMessage)
            }
        }

        override fun onCompleted() {

        }
    }

    private val cardsObserver = object : Runner.RxWrapperListener<DeckBucket> {
        override fun onNext(bucket: DeckBucket) {
            logger.d()
            decksView.deckLoaded(bucket)
        }

        override fun onError(e: Throwable) {

        }

        override fun onCompleted() {

        }
    }

}