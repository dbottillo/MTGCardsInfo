package com.dbottillo.mtgsearchfree.dagger

import android.content.Context
import com.dbottillo.mtgsearchfree.AppNavigator
import com.dbottillo.mtgsearchfree.AppPreferences
import com.dbottillo.mtgsearchfree.AppPreferencesImpl
import com.dbottillo.mtgsearchfree.Navigator
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideNavigator(): Navigator {
        return AppNavigator()
    }

    @Provides
    @Singleton
    fun provideAppPreferences(context: Context, cardsPreferences: CardsPreferences): AppPreferences {
        return AppPreferencesImpl(context, cardsPreferences)
    }
}