package com.dbottillo.mtgsearchfree.dagger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.dbottillo.mtgsearchfree.MTGApp;
import com.dbottillo.mtgsearchfree.mapper.DeckMapper;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.database.CardDataSource;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.presenter.Runner;
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory;
import com.dbottillo.mtgsearchfree.presenter.RunnerAndMap;
import com.dbottillo.mtgsearchfree.util.FileLoaderImpl;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.Logger;

import java.util.List;

import javax.inject.Named;
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
    @Named("cardsDB")
    @Singleton
    SQLiteDatabase provideCardsDatabase(MTGDatabaseHelper mtgDatabaseHelper){
        return mtgDatabaseHelper.getReadableDatabase();
    }

    @Provides
    @Named("storageDB")
    @Singleton
    SQLiteDatabase provideStorageDatabase(CardsInfoDbHelper cardsInfoDbHelper){
        return cardsInfoDbHelper.getWritableDatabase();
    }

    @Provides
    @Singleton
    MTGCardDataSource provideMTGCardDataSource(@Named("cardsDB") SQLiteDatabase database, CardDataSource cardDataSource) {
        return new MTGCardDataSource(database, cardDataSource);
    }

    @Provides
    RunnerFactory provideRxWrapperFactory() {
        return new RunnerFactory();
    }

    @Provides
    @Singleton
    DeckMapper provideDeckMapper() {
        return new DeckMapper();
    }

    @Provides
    RunnerAndMap<List<MTGCard>, DeckBucket> provideDeckBucketWrapper() {
        return new RunnerAndMap<>();
    }

    @Provides
    Runner<int[]> provideFavWrapper() {
        return new Runner<>();
    }

    @Provides
    FileUtil provideFileUtil() {
        return new FileUtil(new FileLoaderImpl(app.getApplicationContext()));
    }

    @Provides
    Logger provideLogger(){
        return new Logger();
    }
}
