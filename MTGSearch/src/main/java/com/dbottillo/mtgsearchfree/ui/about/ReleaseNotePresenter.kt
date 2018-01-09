package com.dbottillo.mtgsearchfree.ui.about

import com.dbottillo.mtgsearchfree.interactors.ReleaseNoteInteractor

class ReleaseNotePresenter constructor(val interactor: ReleaseNoteInteractor) {

    lateinit var view: ReleaseNoteView

    fun init(view: ReleaseNoteView) {
        this.view = view
    }

    fun load() {
        interactor.load().subscribe({
            view.showItems(it)
        }, {
            view.showError(it.localizedMessage)
        })
    }
}

interface ReleaseNoteView {
    fun showItems(list: List<ReleaseNoteItem>)
    fun showError(message: String)
}

data class ReleaseNoteItem(val title: String, val lines: List<String>)