package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractorImpl;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractorImpl;
import com.dbottillo.mtgsearchfree.model.database.SetDataSource;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;
import com.dbottillo.mtgsearchfree.model.storage.SavedCardsStorage;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.Logger;

import dagger.Module;
import dagger.Provides;

@Module
class InteractorsModule {

    @Provides
    CardFilterInteractor provideCardFilterInteractor(CardsPreferences cardsPreferences, Logger logger) {
        return new CardFilterInteractorImpl(cardsPreferences, logger);
    }

    @Provides
    CardsInteractor provideCardsInteractor(CardsStorage cardsStorage, Logger logger) {
        return new CardsInteractorImpl(cardsStorage, logger);
    }

    @Provides
    SetsInteractor provideSetsInteractor(SetDataSource setDataSource, Logger logger) {
        return new SetsInteractorImpl(setDataSource, logger);
    }

    @Provides
    PlayerInteractor providePlayerInteractor(PlayersStorage playersStorage, Logger logger) {
        return new PlayerInteractorImpl(playersStorage, logger);
    }

    @Provides
    DecksInteractor provideDecksInteractor(DecksStorage decksStorage, FileUtil fileUtil, Logger logger) {
        return new DecksInteractorImpl(decksStorage, fileUtil, logger);
    }

    @Provides
    SavedCardsInteractor provideSavedCardsInteractor(SavedCardsStorage storage, Logger logger){
        return new SavedCardsInteractorImpl(storage, logger);
    }
}

