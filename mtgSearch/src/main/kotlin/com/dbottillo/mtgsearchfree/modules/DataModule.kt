package com.dbottillo.mtgsearchfree.modules

import android.content.SharedPreferences
import com.dbottillo.mtgsearchfree.base.MTGApp
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.model.storage.SetsStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(app: MTGApp): SharedPreferences {
        return app.getSharedPreferences(MTGApp.PREFS_NAME, 0);
    }

    @Provides
    @Singleton
    fun provideCardFilterStorage(pref: SharedPreferences): CardFilterStorage {
        return CardFilterStorage(pref);
    }

    @Provides
    @Singleton
    fun provideCardsStorage(): CardsStorage {
        return CardsStorage();
    }

    @Provides
    @Singleton
    fun provideSetsStrorage(): SetsStorage {
        return SetsStorage();
    }
}