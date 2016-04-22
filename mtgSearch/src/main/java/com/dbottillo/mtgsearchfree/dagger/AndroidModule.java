package com.dbottillo.mtgsearchfree.dagger;

import android.content.Context;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.presenter.RxWrapper;

import java.util.List;

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

    @Provides
    @Singleton
    MTGDatabaseHelper provideMTGDatabaseHelper() {
        return new MTGDatabaseHelper(app);
    }

    @Provides
    @Singleton
    CardsInfoDbHelper provideCardsInfoDatabaseHelper() {
        return new CardsInfoDbHelper(app);
    }

    @Provides
    RxWrapper<List<MTGSet>> provideListMTGSetWrapper() {
        return new RxWrapper<>();
    }

    @Provides
    RxWrapper<CardFilter> provideCardFilterWrapper() {
        return new RxWrapper<>();
    }

    @Provides
    RxWrapper<List<Player>> provideLisPlayerWrapper() {
        return new RxWrapper<>();
    }

    @Provides
    RxWrapper<List<MTGCard>> provideListCardWrapper() {
        return new RxWrapper<>();
    }

    @Provides
    RxWrapper<List<Deck>> provideListDeckWrapper() {
        return new RxWrapper<>();
    }

    @Provides
    RxWrapper<int[]> provideFavWrapper() {
        return new RxWrapper<>();
    }
}
