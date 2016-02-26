package com.dbottillo.mtgsearchfree.modules

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractorImpl
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenterImpl
import dagger.Module
import dagger.Provides

@Module
class CardFilterModule() {


    @Provides
    fun providePresenter(interactor: CardFilterInteractor): CardFilterPresenter {
        return CardFilterPresenterImpl(interactor);
    }

/*    @Provides
    fun providePresenter(): CardFilterPresenter {
        return CardFilterPresenterImpl(CardFilterInteractorImpl(CardFilterStorage()));
    }*/
    /*
        @Provides
        fun providePresenter(cardFilterView: CardFilterView, interactor: CardFilterInteractor): CardFilterPresenter {
            return CardFilterPresenterImpl(cardFilterView, interactor)
        }*/
}