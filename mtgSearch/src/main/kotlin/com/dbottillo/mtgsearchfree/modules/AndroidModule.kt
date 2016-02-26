package com.dbottillo.mtgsearchfree.modules

import android.content.Context
import com.dbottillo.mtgsearchfree.base.MTGApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AndroidModule(private val app: MTGApp) {

    @Provides @Singleton fun provideContext(): Context {
        return app.applicationContext;
    }

    @Provides @Singleton fun provideApplication(): MTGApp {
        return app;
    }

}