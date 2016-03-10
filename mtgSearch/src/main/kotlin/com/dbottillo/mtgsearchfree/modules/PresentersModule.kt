package com.dbottillo.mtgsearchfree.modules

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.presenter.*
import dagger.Module
import dagger.Provides

@Module
class PresentersModule() {

    @Provides
    fun provideCardFilterPresenter(interactor: CardFilterInteractor): CardFilterPresenter {
        return CardFilterPresenterImpl(interactor);
    }

    @Provides
    fun provideCardsPresenter(interactor: CardsInteractor): CardsPresenter {
        return CardsPresenterImpl(interactor);
    }

    @Provides
    fun provideSetsPresenter(interactor: SetsInteractor): SetsPresenter {
        return SetsPresenterImpl(interactor);
    }
}