package com.dbottillo.mtgsearchfree.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.base.MTGApp;
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;
import com.dbottillo.mtgsearchfree.model.storage.SetsStorage;

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
    CardsStorage provideCardsStorage(Context context) {
        return new CardsStorage(context);
    }

    @Provides
    @Singleton
    SetsStorage provideSetsStorage(Context context) {
        return new SetsStorage(context);
    }
}