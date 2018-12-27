package com.dbottillo.mtgsearchfree.dagger

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper
import com.dbottillo.mtgsearchfree.database.CardDataSource
import com.dbottillo.mtgsearchfree.util.DialogUtil
import com.dbottillo.mtgsearchfree.util.FileManager
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.Logger
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class CoreModule {

    @Provides
    @Singleton
    fun provideContext(app: Application): Context {
        return app.applicationContext
    }

    @Provides
    @Singleton
    fun provideLogger(): Logger {
        return Logger()
    }

    @Provides
    fun provideFileUtil(fileManager: FileManager): FileUtil {
        return FileUtil(fileManager)
    }

    @Provides
    @Singleton
    fun provideMTGDatabaseHelper(app: Application): MTGDatabaseHelper {
        return MTGDatabaseHelper(app)
    }

    @Provides
    @Singleton
    fun provideCardsInfoDatabaseHelper(app: Application): CardsInfoDbHelper {
        return CardsInfoDbHelper(app)
    }

    @Provides
    @Named("cardsDB")
    @Singleton
    fun provideCardsDatabase(mtgDatabaseHelper: MTGDatabaseHelper): SQLiteDatabase {
        return mtgDatabaseHelper.readableDatabase
    }

    @Provides
    @Named("storageDB")
    @Singleton
    fun provideStorageDatabase(cardsInfoDbHelper: CardsInfoDbHelper): SQLiteDatabase {
        return cardsInfoDbHelper.writableDatabase
    }

    @Provides
    @Singleton
    fun provideMTGCardDataSource(@Named("cardsDB") database: SQLiteDatabase, cardDataSource: CardDataSource): MTGCardDataSource {
        return MTGCardDataSource(database, cardDataSource)
    }

    @Provides
    fun provideDialogUtil(): DialogUtil {
        return DialogUtil()
    }
}