package com.dbottillo.mtgsearchfree.dagger

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.database.CardDataSource
import com.dbottillo.mtgsearchfree.database.DeckColorMapper
import com.dbottillo.mtgsearchfree.database.DeckDataSource
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource
import com.dbottillo.mtgsearchfree.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.database.PlayerDataSource
import com.dbottillo.mtgsearchfree.database.SetDataSource
import com.dbottillo.mtgsearchfree.releasenote.ReleaseNoteStorage
import com.dbottillo.mtgsearchfree.storage.CardsHelper
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.storage.CardsPreferencesImpl
import com.dbottillo.mtgsearchfree.storage.CardsStorage
import com.dbottillo.mtgsearchfree.storage.CardsStorageImpl
import com.dbottillo.mtgsearchfree.storage.DecksStorage
import com.dbottillo.mtgsearchfree.storage.DecksStorageImpl
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.storage.GeneralPreferences
import com.dbottillo.mtgsearchfree.lifecounter.PlayersStorage
import com.dbottillo.mtgsearchfree.lifecounter.PlayersStorageImpl
import com.dbottillo.mtgsearchfree.storage.SavedCardsStorage
import com.dbottillo.mtgsearchfree.storage.SavedCardsStorageImpl
import com.dbottillo.mtgsearchfree.util.AppInfo
import com.dbottillo.mtgsearchfree.util.FileManager
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.Logger
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
open class DataModule {

    @Provides
    @Singleton
    fun providesAppInfo(context: Context): AppInfo {
        return AppInfo(context)
    }

    @Provides
    @Singleton
    open fun providesGeneralData(context: Context, appInfo: AppInfo): GeneralData {
        return GeneralPreferences(context, appInfo)
    }

    @Provides
    @Singleton
    open fun provideGeneralPreferences(context: Context): CardsPreferences {
        return CardsPreferencesImpl(context)
    }

    @Provides
    @Singleton
    fun providesCardDataSource(@Named("storageDB") database: SQLiteDatabase, gson: Gson): CardDataSource {
        return CardDataSource(database, gson)
    }

    @Provides
    @Singleton
    fun provideFavouritesDataSource(@Named("storageDB") database: SQLiteDatabase, cardDataSource: CardDataSource): FavouritesDataSource {
        return FavouritesDataSource(database, cardDataSource)
    }

    @Provides
    @Singleton
    fun provideCardsStorage(
        mtgCardDataSource: MTGCardDataSource,
        favouritesDataSource: FavouritesDataSource,
        cardsPreferences: CardsPreferences,
        cardsHelper: CardsHelper,
        logger: Logger
    ): CardsStorage {
        return CardsStorageImpl(mtgCardDataSource,
                favouritesDataSource, cardsPreferences, cardsHelper, logger)
    }

    @Provides
    @Singleton
    fun provideSetDataSource(@Named("cardsDB") database: SQLiteDatabase): SetDataSource {
        return SetDataSource(database)
    }

    @Provides
    @Singleton
    fun providePlayerDataSource(@Named("storageDB") database: SQLiteDatabase): PlayerDataSource {
        return PlayerDataSource(database)
    }

    @Provides
    @Singleton
    fun providePlayerStorage(playerDataSource: PlayerDataSource, logger: Logger): PlayersStorage {
        return PlayersStorageImpl(playerDataSource, logger)
    }

    @Provides
    @Singleton
    fun provideDeckDataSource(
        @Named("storageDB") database: SQLiteDatabase,
        cardDataSource: CardDataSource,
        mtgCardDataSource: MTGCardDataSource,
        deckColorMapper: DeckColorMapper,
        logger: Logger
    ): DeckDataSource {
        return DeckDataSource(database, cardDataSource, mtgCardDataSource, deckColorMapper, logger)
    }

    @Provides
    @Singleton
    fun provideDecksStorage(fileUtil: FileUtil, deckDataSource: DeckDataSource, generalData: GeneralData, logger: Logger): DecksStorage {
        return DecksStorageImpl(fileUtil, deckDataSource, generalData, logger)
    }

    @Provides
    @Singleton
    fun provideSavedCardsStorage(
        favouritesDataSource: FavouritesDataSource,
        cardsHelper: CardsHelper,
        cardsPreferences: CardsPreferences,
        logger: Logger
    ): SavedCardsStorage {
        return SavedCardsStorageImpl(favouritesDataSource, cardsHelper, cardsPreferences, logger)
    }

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideCardsHelper(): CardsHelper {
        return CardsHelper()
    }

    @Provides
    @Singleton
    fun provideReleaseNoteStorage(fileManager: FileManager, gson: Gson): ReleaseNoteStorage {
        return ReleaseNoteStorage(fileManager, gson)
    }
}