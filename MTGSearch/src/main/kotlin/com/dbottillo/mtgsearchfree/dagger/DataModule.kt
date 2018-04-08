package com.dbottillo.mtgsearchfree.dagger

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import com.dbottillo.mtgsearchfree.model.database.*
import com.dbottillo.mtgsearchfree.model.storage.*
import com.dbottillo.mtgsearchfree.util.*
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
    fun provideCardsStorage(mtgCardDataSource: MTGCardDataSource,
                            favouritesDataSource: FavouritesDataSource,
                            cardsPreferences: CardsPreferences,
                            cardsHelper: CardsHelper,
                            logger: Logger): CardsStorage {
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
    fun provideDeckDataSource(@Named("storageDB") database: SQLiteDatabase, cardDataSource: CardDataSource, mtgCardDataSource: MTGCardDataSource): DeckDataSource {
        return DeckDataSource(database, cardDataSource, mtgCardDataSource)
    }

    @Provides
    @Singleton
    fun provideDecksStorage(fileUtil: FileUtil, deckDataSource: DeckDataSource, logger: Logger): DecksStorage {
        return DecksStorageImpl(fileUtil, deckDataSource, logger)
    }

    @Provides
    @Singleton
    fun provideSavedCardsStorage(favouritesDataSource: FavouritesDataSource, cardsHelper: CardsHelper,
                                 cardsPreferences: CardsPreferences, logger: Logger): SavedCardsStorage {
        return SavedCardsStorageImpl(favouritesDataSource, cardsHelper, cardsPreferences, logger)
    }

    @Provides
    @Singleton
    fun providesGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun providesGsonUtil(gson: Gson): GsonUtil {
        return GsonUtil(gson)
    }

    @Provides
    @Singleton
    fun provideCardsHelper(): CardsHelper {
        return CardsHelper()
    }

    @Provides
    @Singleton
    fun provideReleaseNoteStorage(fileManager: FileManager, gsonUtil: GsonUtil): ReleaseNoteStorage {
        return ReleaseNoteStorage(fileManager, gsonUtil)
    }
}