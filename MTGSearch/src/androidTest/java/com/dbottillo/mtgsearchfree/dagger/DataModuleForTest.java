package com.dbottillo.mtgsearchfree.dagger;

import android.content.Context;

import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferencesForTest;
import com.dbottillo.mtgsearchfree.model.storage.GeneralData;
import com.dbottillo.mtgsearchfree.model.storage.GeneralPreferencesForTest;

import javax.inject.Singleton;

import dagger.Provides;

public class DataModuleForTest extends DataModule {

    @Provides
    @Singleton
    GeneralData provideSharedPreferences(Context context) {
        return new GeneralPreferencesForTest();
    }

    @Provides
    @Singleton
    CardsPreferences provideGeneralPreferences(Context context) {
        return new CardsPreferencesForTest(context);
    }
}
