package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractorImpl;

import dagger.Module;
import dagger.Provides;

@Module
class InteractorsModule {

    @Provides
    CardFilterInteractor provideCardFilterInteractor(CardFilterStorage filterStorage) {
        return new CardFilterInteractorImpl(filterStorage);
    }

    @Provides
    CardsInteractor provideCardsInteractor(CardsStorage cardsStorage) {
        return new CardsInteractorImpl(cardsStorage);
    }

    @Provides
    SetsInteractor provideSetsInteractor(SetsStorage setsStorage) {
        return new SetsInteractorImpl(setsStorage);
    }
}

