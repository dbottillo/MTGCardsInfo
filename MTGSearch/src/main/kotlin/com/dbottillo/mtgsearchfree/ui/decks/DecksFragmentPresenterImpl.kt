package com.dbottillo.mtgsearchfree.ui.decks

import android.net.Uri
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class DecksFragmentPresenterImpl @Inject constructor(
    private val interactor: DecksInteractor,
    private val logger: Logger
) : DecksFragmentPresenter {
    lateinit var decksView: DecksFragmentView

    private var disposable: CompositeDisposable = CompositeDisposable()

    init {
        logger.d("created")
    }

    override fun init(view: DecksFragmentView) {
        logger.d()
        decksView = view
    }

    override fun loadDecks() {
        logger.d()
        disposable.add(interactor.load().subscribe({
            decksView.decksLoaded(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    override fun addDeck(name: String) {
        logger.d("add $name")
        disposable.add(interactor.addDeck(name).subscribe({
            decksView.decksLoaded(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    override fun deleteDeck(deck: Deck) {
        logger.d("delete $deck")
        disposable.add(interactor.deleteDeck(deck).subscribe({
            decksView.decksLoaded(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    override fun importDeck(uri: Uri) {
        logger.d("import $uri")
        disposable.add(interactor.importDeck(uri).subscribe({
            decksView.decksLoaded(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    override fun copyDeck(deck: Deck) {
        logger.d("copy $deck")
        disposable.add(interactor.copy(deck).subscribe({
            decksView.decksLoaded(it)
        }, {
            showError(it)
            logger.logNonFatal(it)
        }))
    }

    internal fun showError(e: Throwable) {
        if (e is MTGException) {
            decksView.showError(e.message)
        } else {
            decksView.showError(e.localizedMessage)
        }
    }

    override fun onDestroy() {
        disposable.clear()
    }
}