package com.dbottillo.mtgsearchfree.modules

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenterImpl
import com.dbottillo.mtgsearchfree.view.CardFilterView
import dagger.Module
import dagger.Provides

@Module
class CardFilterModule(private var view: CardFilterView) {

    @Provides fun provideView(): CardFilterView {
        return view;
    }

    @Provides fun providePresenter(cardFilterView: CardFilterView, interactor: CardFilterInteractor): CardFilterPresenter {
        return CardFilterPresenterImpl(cardFilterView, interactor)
    }
}