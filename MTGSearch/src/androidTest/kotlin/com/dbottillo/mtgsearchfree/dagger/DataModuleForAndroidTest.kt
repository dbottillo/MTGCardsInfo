package com.dbottillo.mtgsearchfree.dagger

import android.content.Context
import android.content.SharedPreferences
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferencesForAndroidTest
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.model.storage.GeneralPreferencesForAndroidTest
import com.dbottillo.mtgsearchfree.util.AppInfo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModuleForAndroidTest : DataModule() {

    @Provides
    @Singleton
    override fun providesGeneralData(sharedPreferences: SharedPreferences, appInfo: AppInfo): GeneralData {
        return GeneralPreferencesForAndroidTest()
    }

    @Provides
    @Singleton
    override fun provideGeneralPreferences(context: Context): CardsPreferences {
        return CardsPreferencesForAndroidTest(context)
    }
}
