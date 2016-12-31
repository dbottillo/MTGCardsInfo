package com.dbottillo.mtgsearchfree.dagger;

import android.content.Context;
import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferencesForTest;
import com.dbottillo.mtgsearchfree.model.storage.GeneralData;
import com.dbottillo.mtgsearchfree.model.storage.GeneralPreferencesForTest;
import com.dbottillo.mtgsearchfree.util.AppInfo;

import javax.inject.Singleton;

import dagger.Provides;

public class DataModuleForTest extends DataModule {

    @Provides
    @Singleton
    GeneralData providesGeneralData(SharedPreferences sharedPreferences, AppInfo appInfo) {
        return new GeneralPreferencesForTest();
    }

    @Provides
    @Singleton
    CardsPreferences provideGeneralPreferences(Context context) {
        return new CardsPreferencesForTest(context);
    }
}
