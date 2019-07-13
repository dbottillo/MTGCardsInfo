package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractorImpl
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.CardsInteractorImpl
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractorImpl
import com.dbottillo.mtgsearchfree.interactor.SchedulerProvider
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractorImpl
import com.dbottillo.mtgsearchfree.storage.SetDataSource
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.interactors.DecksInteractorImpl
import com.dbottillo.mtgsearchfree.lifecounter.PlayerInteractor
import com.dbottillo.mtgsearchfree.lifecounter.PlayerInteractorImpl
import com.dbottillo.mtgsearchfree.lifecounter.PlayersStorage
import com.dbottillo.mtgsearchfree.repository.CardRepository
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.storage.CardsStorage
import com.dbottillo.mtgsearchfree.storage.DecksStorage
import com.dbottillo.mtgsearchfree.storage.SavedCardsStorage
import com.dbottillo.mtgsearchfree.util.FileManager
import com.dbottillo.mtgsearchfree.util.Logger
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Module
class InteractorsModule {

    @Provides
    @Singleton
    fun providePlayerInteractor(
        playersStorage: PlayersStorage,
        schedulerProvider: SchedulerProvider,
        logger: Logger
    ): PlayerInteractor {
        return PlayerInteractorImpl(playersStorage, schedulerProvider, logger)
    }

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
        logger: Logger,
        cardRepository: CardRepository
    ): CardsInteractor {
        return CardsInteractorImpl(cardsStorage, fileManager, schedulerProvider, logger, cardRepository)
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
    @Singleton
    fun provideSchedulerProvider(): SchedulerProvider {
        return object : SchedulerProvider {
            override fun ui(): Scheduler {
                return AndroidSchedulers.mainThread()
            }

            override fun io(): Scheduler {
                return Schedulers.io()
            }
        }
    }
}
