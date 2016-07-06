package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractorImpl;
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;
import com.dbottillo.mtgsearchfree.model.storage.SetsStorage;

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

    @Provides
    PlayerInteractor providePlayerInteractor(PlayersStorage playersStorage) {
        return new PlayerInteractorImpl(playersStorage);
    }

    @Provides
    DecksInteractor provideDecksInteractor(DecksStorage decksStorage) {
        return new DecksInteractorImpl(decksStorage);
    }
}

