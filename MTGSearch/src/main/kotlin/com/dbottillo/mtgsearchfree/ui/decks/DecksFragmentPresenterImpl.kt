package com.dbottillo.mtgsearchfree.ui.decks

import android.net.Uri
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.util.Logger
import javax.inject.Inject

class DecksFragmentPresenterImpl @Inject constructor(
    private val interactor: DecksInteractor,
    private val logger: Logger
) : DecksFragmentPresenter {
    lateinit var decksView: DecksFragmentView

    init {
        logger.d("created")
    }

    override fun init(view: DecksFragmentView) {
        logger.d()
        decksView = view
    }

    override fun loadDecks() {
        logger.d()
        interactor.load().subscribe({
            decksView.decksLoaded(it)
        }, {
            showError(it)
        })
    }

    override fun addDeck(name: String) {
        logger.d("add " + name)
        interactor.addDeck(name).subscribe({
            decksView.decksLoaded(it)
        }, {
            showError(it)
        })
    }

    override fun deleteDeck(deck: Deck) {
        logger.d("delete " + deck)
        interactor.deleteDeck(deck).subscribe({
            decksView.decksLoaded(it)
        }, {
            showError(it)
        })
    }

    override fun importDeck(uri: Uri) {
        logger.d("import " + uri.toString())
        interactor.importDeck(uri).subscribe({
            decksView.decksLoaded(it)
        }, {
            showError(it)
        })
    }

    override fun copyDeck(deck: Deck) {
        logger.d("copy $deck")
        interactor.copy(deck).subscribe({
            decksView.decksLoaded(it)
        }, {
            showError(it)
        })
    }

    internal fun showError(e: Throwable) {
        if (e is MTGException) {
            decksView.showError(e.message)
        } else {
            decksView.showError(e.localizedMessage)
        }
    }
}