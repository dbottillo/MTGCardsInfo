package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorFragment
import com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorPresenter
import com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorPresenterImpl
import com.dbottillo.mtgsearchfree.ui.decks.DecksFragment
import com.dbottillo.mtgsearchfree.ui.decks.DecksFragmentPresenter
import com.dbottillo.mtgsearchfree.ui.decks.DecksFragmentPresenterImpl
import com.dbottillo.mtgsearchfree.ui.decks.addToDeck.AddToDeckFragment
import com.dbottillo.mtgsearchfree.ui.decks.addToDeck.AddToDeckInteractor
import com.dbottillo.mtgsearchfree.ui.decks.addToDeck.AddToDeckPresenter
import com.dbottillo.mtgsearchfree.ui.decks.addToDeck.AddToDeckPresenterImpl
import com.dbottillo.mtgsearchfree.ui.decks.deck.DeckFragment
import com.dbottillo.mtgsearchfree.ui.decks.startingHand.DeckStartingHandFragment
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterFragment
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterPresenter
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterPresenterImpl
import com.dbottillo.mtgsearchfree.ui.saved.SavedCardsPresenter
import com.dbottillo.mtgsearchfree.ui.saved.SavedCardsPresenterImpl
import com.dbottillo.mtgsearchfree.ui.saved.SavedFragment
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragment
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragmentPresenter
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragmentPresenterImpl
import com.dbottillo.mtgsearchfree.util.Logger
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {
    @ActivityScope
    @ContributesAndroidInjector(modules = [SetsFragmentModule::class])
    abstract fun contributeSetsFragmentInjector(): SetsFragment

    @ActivityScope
    @ContributesAndroidInjector(modules = [DecksFragmentModule::class])
    abstract fun contributeDecksFragmentInjector(): DecksFragment

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeDeckFragmentInjector(): DeckFragment

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeDeckStartingHandFragmentInjector(): DeckStartingHandFragment

    @ActivityScope
    @ContributesAndroidInjector(modules = [SavedFragmentModule::class])
    abstract fun contributeSavedFragmentInjector(): SavedFragment

    @ActivityScope
    @ContributesAndroidInjector(modules = [LifeCounterFragmentModule::class])
    abstract fun contributeLifeCounterFragmentInjector(): LifeCounterFragment

    @ActivityScope
    @ContributesAndroidInjector(modules = [AddToDeckFragmentModule::class])
    abstract fun contributeAddToDeckFragmentInjector(): AddToDeckFragment

    @ActivityScope
    @ContributesAndroidInjector(modules = [CardsConfiguratorFragmentModule::class])
    abstract fun contributeCardsConfiguratorFragmentInjector(): CardsConfiguratorFragment
}

@Module
class SetsFragmentModule {
    @Provides
    fun providesSetsFragmentPresenter(
        setsInteractor: SetsInteractor,
        cardsInteractor: CardsInteractor,
        cardsPreferences: CardsPreferences,
        generalData: GeneralData,
        logger: Logger
    ): SetsFragmentPresenter {
        return SetsFragmentPresenterImpl(setsInteractor, cardsInteractor, cardsPreferences, generalData, logger)
    }
}

@Module
class DecksFragmentModule {
    @Provides
    fun providesDecksFragmentPresenter(
        interactor: DecksInteractor,
        logger: Logger
    ): DecksFragmentPresenter {
        return DecksFragmentPresenterImpl(interactor, logger)
    }
}

@Module
class SavedFragmentModule {
    @Provides
    fun providesSavedFragmentPresenter(
        interactor: SavedCardsInteractor,
        generalData: GeneralData,
        logger: Logger
    ): SavedCardsPresenter {
        return SavedCardsPresenterImpl(interactor, generalData, logger)
    }
}

@Module
class LifeCounterFragmentModule {
    @Provides
    fun providesLifeCounterFragmentPresenter(
        interactor: PlayerInteractor,
        logger: Logger
    ): LifeCounterPresenter {
        return LifeCounterPresenterImpl(interactor, logger)
    }
}

@Module
class AddToDeckFragmentModule {
    @Provides
    fun provideDecksPresenter(interactor: AddToDeckInteractor, logger: Logger): AddToDeckPresenter {
        return AddToDeckPresenterImpl(interactor, logger)
    }
}

@Module
class CardsConfiguratorFragmentModule {
    @Provides
    fun providesCardsConfiguratorPresenter(interactor: CardFilterInteractor): CardsConfiguratorPresenter {
        return CardsConfiguratorPresenterImpl(interactor)
    }
}