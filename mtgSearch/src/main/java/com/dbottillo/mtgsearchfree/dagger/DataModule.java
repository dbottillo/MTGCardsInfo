package com.dbottillo.mtgsearchfree.dagger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.model.database.SetDataSource;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferencesImpl;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.model.storage.GeneralData;
import com.dbottillo.mtgsearchfree.model.storage.GeneralPreferences;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;
import com.dbottillo.mtgsearchfree.presenter.MemoryStorage;
import com.dbottillo.mtgsearchfree.util.FileUtil;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModule {

    @Provides
    @Singleton
    GeneralData provideSharedPreferences(Context context) {
        return new GeneralPreferences(context);
    }

    @Provides
    @Singleton
    CardsPreferences provideGeneralPreferences(Context context) {
        return new CardsPreferencesImpl(context);
    }

    @Provides
    @Singleton
    CardsStorage provideCardsStorage(MTGCardDataSource mtgCardDataSource, CardsInfoDbHelper cardsInfoDbHelper) {
        return new CardsStorage(mtgCardDataSource, cardsInfoDbHelper);
    }

    @Provides
    @Singleton
    SetDataSource provideSetDataSource(@Named("cardsDatabase") SQLiteDatabase database) {
        return new SetDataSource(database);
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

    @Provides
    @Singleton
    MemoryStorage provideMemoryStorage() {
        return new MemoryStorage();
    }

}