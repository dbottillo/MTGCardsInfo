package com.dbottillo.mtgsearchfree.dagger

import com.dbottillo.mtgsearchfree.ActivityScope
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.interactors.ReleaseNoteInteractor
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.storage.GeneralData
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.ui.BasicActivityModule
import com.dbottillo.mtgsearchfree.ui.about.AboutActivity
import com.dbottillo.mtgsearchfree.ui.about.ReleaseNoteActivity
import com.dbottillo.mtgsearchfree.ui.about.ReleaseNotePresenter
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivity
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivityPresenter
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivityPresenterImpl
import com.dbottillo.mtgsearchfree.ui.decks.deck.DeckActivity
import com.dbottillo.mtgsearchfree.ui.decks.deck.DeckActivityPresenter
import com.dbottillo.mtgsearchfree.ui.lucky.CardLuckyActivity
import com.dbottillo.mtgsearchfree.ui.lucky.CardsLuckyPresenter
import com.dbottillo.mtgsearchfree.ui.lucky.CardsLuckyPresenterImpl
import com.dbottillo.mtgsearchfree.ui.search.SearchActivity
import com.dbottillo.mtgsearchfree.ui.search.SearchPresenter
import com.dbottillo.mtgsearchfree.ui.search.SearchPresenterImpl
import com.dbottillo.mtgsearchfree.ui.sets.SetPickerActivity
import com.dbottillo.mtgsearchfree.ui.sets.SetPickerPresenter
import com.dbottillo.mtgsearchfree.ui.sets.SetPickerPresenterImpl
import com.dbottillo.mtgsearchfree.ui.views.CardPresenter
import com.dbottillo.mtgsearchfree.ui.views.CardPresenterImpl
import com.dbottillo.mtgsearchfree.util.Logger
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ActivityScope
    @ContributesAndroidInjector(modules = [(BasicActivityModule::class)])
    abstract fun contributeBaseActivityInjector(): BasicActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(CardsActivityModule::class)])
    abstract fun contributeCardsActivityInjector(): CardsActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(DeckActivityModule::class)])
    abstract fun contributeDeckActivityInjector(): DeckActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(CardsLuckyActivityModule::class)])
    abstract fun contributeCardsLuckyActivityInjector(): CardLuckyActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(SearchActivityModule::class)])
    abstract fun contributeSearchActivityInjector(): SearchActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(ReleaseNoteActivityModule::class)])
    abstract fun contributeReleaseNoteActivityInjector(): ReleaseNoteActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(SetPickerActivityModule::class)])
    abstract fun contributeSetPickerActivityInjector(): SetPickerActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeAboutActivityInjector(): AboutActivity
}

@Module
class CardsActivityModule {
    @Provides
    fun providesCardsActivityPresenter(
        cardsInteractor: CardsInteractor,
        savedCardsInteractor: SavedCardsInteractor,
        decksInteractor: DecksInteractor,
        cardsPreferences: CardsPreferences,
        logger: Logger
    ): CardsActivityPresenter {
        return CardsActivityPresenterImpl(cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences, logger)
    }

    @Provides
    fun provideCardPresenter(interactor: CardsInteractor, logger: Logger): CardPresenter {
        return CardPresenterImpl(interactor, logger)
    }
}

@Module
class CardsLuckyActivityModule {
    @Provides
    fun providesCardsLuckyActivityPresenter(
        cardsInteractor: CardsInteractor,
        cardsPreferences: CardsPreferences,
        logger: Logger
    ): CardsLuckyPresenter {
        return CardsLuckyPresenterImpl(cardsInteractor, cardsPreferences, logger)
    }

    @Provides
    fun provideCardPresenter(interactor: CardsInteractor, logger: Logger): CardPresenter {
        return CardPresenterImpl(interactor, logger)
    }
}

@Module
class DeckActivityModule {
    @Provides
    fun provideDeckActivityPresenter(interactor: DecksInteractor, logger: Logger): DeckActivityPresenter {
        return DeckActivityPresenter(interactor, logger)
    }
}

@Module
class SearchActivityModule {
    @Provides
    fun providesSearchActivityPresenter(
        setsInteratcor: SetsInteractor,
        cardsInteractor: CardsInteractor,
        generalData: GeneralData,
        logger: Logger
    ): SearchPresenter {
        return SearchPresenterImpl(setsInteratcor, cardsInteractor, generalData, logger)
    }
}

@Module
class ReleaseNoteActivityModule {
    @Provides
    fun providesReleaseNotePresenter(interactor: ReleaseNoteInteractor,
                                     logger: Logger): ReleaseNotePresenter {
        return ReleaseNotePresenter(interactor, logger)
    }
}

@Module
class SetPickerActivityModule {
    @Provides
    fun providesSetPickerPresenter(
        setsInteractor: SetsInteractor,
        cardsPreferences: CardsPreferences
    ): SetPickerPresenter {
        return SetPickerPresenterImpl(setsInteractor, cardsPreferences)
    }
}
