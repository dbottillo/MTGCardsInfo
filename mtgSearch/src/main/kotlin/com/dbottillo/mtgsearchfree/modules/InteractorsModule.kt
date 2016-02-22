package com.dbottillo.mtgsearchfree.modules

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractorImpl
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage
import dagger.Module
import dagger.Provides

@Module
class InteractorsModule {
    @Provides fun provideCardFilterInteractor(filterStorage: CardFilterStorage): CardFilterInteractor {
        return CardFilterInteractorImpl(filterStorage)
    }
}

