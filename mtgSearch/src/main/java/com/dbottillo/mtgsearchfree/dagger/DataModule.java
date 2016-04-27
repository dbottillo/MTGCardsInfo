package com.dbottillo.mtgsearchfree.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;
import com.dbottillo.mtgsearchfree.model.storage.SetsStorage;
import com.dbottillo.mtgsearchfree.util.FileUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class DataModule {

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(MTGApp app) {
        return app.getSharedPreferences(MTGApp.PREFS_NAME, 0);
    }

    @Provides
    @Singleton
    CardFilterStorage provideCardFilterStorage(SharedPreferences pref) {
        return new CardFilterStorage(pref);
    }

    @Provides
    @Singleton
    CardsStorage provideCardsStorage(MTGCardDataSource mtgCardDataSource, CardsInfoDbHelper cardsInfoDbHelper) {
        return new CardsStorage(mtgCardDataSource, cardsInfoDbHelper);
    }

    @Provides
    @Singleton
    SetsStorage provideSetsStorage(MTGDatabaseHelper helper) {
        return new SetsStorage(helper);
    }

    @Provides
    @Singleton
    PlayersStorage providePlayerStorage(CardsInfoDbHelper cardsInfoDbHelper) {
        return new PlayersStorage(cardsInfoDbHelper);
    }

    @Provides
    @Singleton
    DecksStorage provideDecksStorage(FileUtil fileUtil, CardsInfoDbHelper cardsInfoDbHelper, MTGCardDataSource mtgCardDataSource) {
        return new DecksStorage(fileUtil, cardsInfoDbHelper, mtgCardDataSource);
    }
}