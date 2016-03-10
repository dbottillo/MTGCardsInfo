package com.dbottillo.mtgsearchfree.modules

import com.dbottillo.mtgsearchfree.interactors.*
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage
import com.dbottillo.mtgsearchfree.model.storage.SetsStorage
import dagger.Module
import dagger.Provides

@Module()
class InteractorsModule {

    @Provides fun provideCardFilterInteractor(filterStorage: CardFilterStorage): CardFilterInteractor {
        return CardFilterInteractorImpl(filterStorage)
    }

    @Provides fun provideCardsInteractor(cardsStorage: CardsStorage): CardsInteractor {
        return CardsInteractorImpl(cardsStorage)
    }

    @Provides fun provideSetsInteractor(setsStorage: SetsStorage): SetsInteractor {
        return SetsInteractorImpl(setsStorage)
    }
}

