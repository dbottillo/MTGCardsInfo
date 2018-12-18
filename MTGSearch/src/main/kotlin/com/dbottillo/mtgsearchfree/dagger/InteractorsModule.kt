package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.interactors.AppSchedulerProvider
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractorImpl
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.CardsInteractorImpl
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.interactors.DecksInteractorImpl
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractorImpl
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractorImpl
import com.dbottillo.mtgsearchfree.interactors.SchedulerProvider
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractorImpl
import com.dbottillo.mtgsearchfree.model.storage.database.SetDataSource
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage
import com.dbottillo.mtgsearchfree.model.storage.SavedCardsStorage
import com.dbottillo.mtgsearchfree.util.FileManager
import com.dbottillo.mtgsearchfree.util.Logger
import dagger.Module
import dagger.Provides

@Module
class InteractorsModule {

    @Provides
    fun provideCardFilterInteractor(
        cardsPreferences: CardsPreferences,
        logger: Logger
    ): CardFilterInteractor {
        return CardFilterInteractorImpl(cardsPreferences, logger)
    }

    @Provides
    fun provideCardsInteractor(
        cardsStorage: CardsStorage,
        fileManager: FileManager,
        schedulerProvider: SchedulerProvider,
        logger: Logger
    ): CardsInteractor {
        return CardsInteractorImpl(cardsStorage, fileManager, schedulerProvider, logger)
    }

    @Provides
    fun provideSetsInteractor(
        setDataSource: SetDataSource,
        schedulerProvider: SchedulerProvider,
        logger: Logger
    ): SetsInteractor {
        return SetsInteractorImpl(setDataSource, schedulerProvider, logger)
    }

    @Provides
    fun providePlayerInteractor(
        playersStorage: PlayersStorage,
        schedulerProvider: SchedulerProvider,
        logger: Logger
    ): PlayerInteractor {
        return PlayerInteractorImpl(playersStorage, schedulerProvider, logger)
    }

    @Provides
    fun provideDecksInteractor(
        decksStorage: DecksStorage,
        fileManager: FileManager,
        schedulerProvider: SchedulerProvider,
        logger: Logger
    ): DecksInteractor {
        return DecksInteractorImpl(decksStorage, fileManager, schedulerProvider, logger)
    }

    @Provides
    fun provideSavedCardsInteractor(
        storage: SavedCardsStorage,
        schedulerProvider: SchedulerProvider,
        logger: Logger
    ): SavedCardsInteractor {
        return SavedCardsInteractorImpl(storage, schedulerProvider, logger)
    }

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider {
        return AppSchedulerProvider()
    }
}
