package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.view.CardFilterView

class CardFilterPresenterImpl constructor(var filterView: CardFilterView, var interactor: CardFilterInteractor) : CardFilterPresenter {

    var filter = CardFilter()

    override fun loadFilter() {
        //filter = interactor.load()
        filterLoaded()
    }

    private fun filterLoaded() {
        filterView.filterLoaded(filter)
    }

    override fun updateW(on: Boolean) {
        filter.white = on
        filterLoaded()
    }

    override fun updateU(on: Boolean) {
        filter.blue = on
        filterLoaded()
    }

    override fun updateB(on: Boolean) {
        filter.black = on
        filterLoaded()
    }

    override fun updateR(on: Boolean) {
        filter.red = on
        filterLoaded()
    }

    override fun updateG(on: Boolean) {
        filter.green = on
        filterLoaded()
    }

    override fun updateArtifact(on: Boolean) {
        filter.artifact = on
        filterLoaded()
    }

    override fun updateLand(on: Boolean) {
        filter.land = on
        filterLoaded()
    }

    override fun updateEldrazi(on: Boolean) {
        filter.eldrazi = on
        filterLoaded()
    }

    override fun updateCommon(on: Boolean) {
        filter.common = on
        filterLoaded()
    }

    override fun updateUncommon(on: Boolean) {
        filter.uncommon = on
        filterLoaded()
    }

    override fun updateRare(on: Boolean) {
        filter.rare = on
        filterLoaded()
    }

    override fun updateMythic(on: Boolean) {
        filter.mythic = on
        filterLoaded()
    }
}