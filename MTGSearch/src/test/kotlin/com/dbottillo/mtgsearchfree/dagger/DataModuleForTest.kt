package com.dbottillo.mtgsearchfree.dagger

import android.content.Context
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferencesForTest
import com.dbottillo.mtgsearchfree.model.storage.GeneralData
import com.dbottillo.mtgsearchfree.model.storage.GeneralPreferencesForTest
import com.dbottillo.mtgsearchfree.util.AppInfo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModuleForTest : DataModule() {

    @Provides
    @Singleton
    override fun providesGeneralData(context: Context, appInfo: AppInfo): GeneralData {
        return GeneralPreferencesForTest()
    }

    @Provides
    @Singleton
    override fun provideGeneralPreferences(context: Context): CardsPreferences {
        return CardsPreferencesForTest(context)
    }
}
