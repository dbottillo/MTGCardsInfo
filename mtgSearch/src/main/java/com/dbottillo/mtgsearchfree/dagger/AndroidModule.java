package com.dbottillo.mtgsearchfree.dagger;

import android.content.Context;

import com.dbottillo.mtgsearchfree.MTGApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {

    private MTGApp app;

    public AndroidModule(MTGApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    MTGApp provideApplication() {
        return app;
    }

}
