package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.AppNavigator
import com.dbottillo.mtgsearchfree.Navigator
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
}