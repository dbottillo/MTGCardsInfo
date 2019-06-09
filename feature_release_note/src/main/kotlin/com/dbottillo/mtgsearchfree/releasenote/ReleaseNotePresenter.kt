package com.dbottillo.mtgsearchfree.releasenote

import com.dbottillo.mtgsearchfree.util.Logger
import io.reactivex.disposables.CompositeDisposable

class ReleaseNotePresenter constructor(
    private val interactor: ReleaseNoteInteractor,
    private val logger: Logger
) {

    lateinit var view: ReleaseNoteView

    private var disposable: CompositeDisposable = CompositeDisposable()

    fun init(view: ReleaseNoteView) {
        this.view = view
    }

    fun load() {
        disposable.add(interactor.load().subscribe({
            view.showItems(it)
        }, {
            logger.logNonFatal(it)
            view.showError(it.localizedMessage)
        }))
    }

    fun onDestroy() {
        disposable.clear()
    }
}

interface ReleaseNoteView {
    fun showItems(list: List<ReleaseNoteItem>)
    fun showError(message: String)
}

data class ReleaseNoteItem(val title: String, val lines: List<String>)