package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.interactors.*
import com.dbottillo.mtgsearchfree.model.database.SetDataSource
import com.dbottillo.mtgsearchfree.model.storage.*
import com.dbottillo.mtgsearchfree.util.FileManager
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.Logger
import dagger.Module
import dagger.Provides

@Module
class InteractorsModule {

    @Provides
    fun provideCardFilterInteractor(cardsPreferences: CardsPreferences,
                                    logger: Logger): CardFilterInteractor {
        return CardFilterInteractorImpl(cardsPreferences, logger)
    }

    @Provides
    fun provideCardsInteractor(cardsStorage: CardsStorage,
                               fileManager: FileManager,
                               schedulerProvider: SchedulerProvider,
                               logger: Logger): CardsInteractor {
        return CardsInteractorImpl(cardsStorage, fileManager, schedulerProvider, logger)
    }

    @Provides
    fun provideSetsInteractor(setDataSource: SetDataSource,
                              schedulerProvider: SchedulerProvider,
                              logger: Logger): SetsInteractor {
        return SetsInteractorImpl(setDataSource, schedulerProvider, logger)
    }

    @Provides
    fun providePlayerInteractor(playersStorage: PlayersStorage,
                                schedulerProvider: SchedulerProvider,
                                logger: Logger): PlayerInteractor {
        return PlayerInteractorImpl(playersStorage, schedulerProvider, logger)
    }

    @Provides
    fun provideDecksInteractor(decksStorage: DecksStorage,
                               fileUtil: FileUtil,
                               schedulerProvider: SchedulerProvider,
                               logger: Logger): DecksInteractor {
        return DecksInteractorImpl(decksStorage, fileUtil, schedulerProvider, logger)
    }

    @Provides
    fun provideSavedCardsInteractor(storage: SavedCardsStorage,
                                    schedulerProvider: SchedulerProvider,
                                    logger: Logger): SavedCardsInteractor {
        return SavedCardsInteractorImpl(storage, schedulerProvider, logger)
    }

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider {
        return AppSchedulerProvider()
    }
}

