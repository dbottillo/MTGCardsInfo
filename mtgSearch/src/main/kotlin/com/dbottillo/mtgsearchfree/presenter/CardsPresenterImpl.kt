package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.helper.LOG
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.view.CardsView

class CardsPresenterImpl(var interactor: CardsInteractor) : CardsPresenter {

    override fun loadCards(set: MTGSet) {

    }

    override fun init(view: CardsView) {
        LOG.e("init called with "+view.toString());
    }

}