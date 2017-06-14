package com.dbottillo.mtgsearchfree.ui.decks

import android.net.Uri
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.presenter.Runner
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory
import com.dbottillo.mtgsearchfree.util.Logger
import javax.inject.Inject

class DecksFragmentPresenterImpl @Inject constructor(private val interactor: DecksInteractor,
                                 runnerFactory: RunnerFactory,
                                 private val logger: Logger) : DecksFragmentPresenter{

    lateinit var decksView: DecksFragmentView
    private val deckWrapper: Runner<List<Deck>> = runnerFactory.simple<List<Deck>>()

    init {
        logger.d("created")
    }

    override fun init(view: DecksFragmentView) {
        logger.d()
        decksView = view
    }

    override fun loadDecks() {
        logger.d()
        deckWrapper.run(interactor.load(), decksObserver)
    }

    override fun addDeck(name: String) {
        logger.d("add " + name)
        deckWrapper.run(interactor.addDeck(name), decksObserver)
    }

    override fun deleteDeck(deck: Deck) {
        logger.d("delete " + deck)
        deckWrapper.run(interactor.deleteDeck(deck), decksObserver)
    }

    override fun importDeck(uri: Uri) {
        logger.d("import " + uri.toString())
        deckWrapper.run(interactor.importDeck(uri), decksObserver)
    }

    private val decksObserver = object : Runner.RxWrapperListener<List<Deck>> {
        override fun onNext(decks: List<Deck>) {
            logger.d()
            decksView.decksLoaded(decks)
        }

        override fun onError(e: Throwable) {
            if (e is MTGException) {
                decksView.showError(e.message)
            } else {
                decksView.showError(e.localizedMessage)
            }
        }

        override fun onCompleted() {

        }
    }

}