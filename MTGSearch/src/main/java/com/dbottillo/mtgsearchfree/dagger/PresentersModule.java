package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.interactors.ReleaseNoteInteractor;
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.model.storage.GeneralData;
import com.dbottillo.mtgsearchfree.ui.about.ReleaseNotePresenter;
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivityPresenter;
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivityPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorPresenter;
import com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.decks.AddToDeckInteractor;
import com.dbottillo.mtgsearchfree.ui.decks.AddToDeckPresenter;
import com.dbottillo.mtgsearchfree.ui.decks.AddToDeckPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.decks.DeckActivityPresenter;
import com.dbottillo.mtgsearchfree.ui.decks.DecksFragmentPresenter;
import com.dbottillo.mtgsearchfree.ui.decks.DecksFragmentPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterPresenter;
import com.dbottillo.mtgsearchfree.ui.lifecounter.LifeCounterPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.lucky.CardsLuckyPresenter;
import com.dbottillo.mtgsearchfree.ui.lucky.CardsLuckyPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.saved.SavedCardsPresenter;
import com.dbottillo.mtgsearchfree.ui.saved.SavedCardsPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.search.SearchPresenter;
import com.dbottillo.mtgsearchfree.ui.search.SearchPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.sets.SetPickerPresenter;
import com.dbottillo.mtgsearchfree.ui.sets.SetPickerPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragmentPresenter;
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragmentPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.views.CardPresenter;
import com.dbottillo.mtgsearchfree.ui.views.CardPresenterImpl;
import com.dbottillo.mtgsearchfree.util.Logger;

import dagger.Module;
import dagger.Provides;

@Module
public class PresentersModule {

    @Provides
    LifeCounterPresenter providePlayerPresenter(PlayerInteractor interactor, Logger logger) {
        return new LifeCounterPresenterImpl(interactor, logger);
    }

    @Provides
    AddToDeckPresenter provideDecksPresenter(AddToDeckInteractor interactor, Logger logger) {
        return new AddToDeckPresenterImpl(interactor, logger);
    }

    @Provides
    DeckActivityPresenter provideDeckActivityPresenter(DecksInteractor interactor, Logger logger) {
        return new DeckActivityPresenter(interactor, logger);
    }

    @Provides
    DecksFragmentPresenter provideDecksFragmentPresenter(DecksInteractor interactor, Logger logger) {
        return new DecksFragmentPresenterImpl(interactor, logger);
    }

    @Provides
    CardPresenter provideCardPresenter(CardsInteractor interactor, Logger logger) {
        return new CardPresenterImpl(interactor, logger);
    }

    @Provides
    SavedCardsPresenter provideSavedCardsPresenter(SavedCardsInteractor interactor,
                                                   GeneralData generalData,
                                                   Logger logger) {
        return new SavedCardsPresenterImpl(interactor, generalData, logger);
    }

    @Provides
    SetsFragmentPresenter providesSetsFragmentPresenter(SetsInteractor setsInteractor,
                                                        CardsInteractor cardsInteractor,
                                                        CardsPreferences cardsPreferences,
                                                        GeneralData generalData,
                                                        Logger logger) {
        return new SetsFragmentPresenterImpl(setsInteractor, cardsInteractor, cardsPreferences, generalData, logger);
    }

    @Provides
    CardsConfiguratorPresenter providesCardsConfiguratorPresenter(CardFilterInteractor interactor) {
        return new CardsConfiguratorPresenterImpl(interactor);
    }

    @Provides
    SetPickerPresenter providesSetPickerPresenter(SetsInteractor setsInteractor,
                                                  CardsPreferences cardsPreferences,
                                                  Logger logger) {
        return new SetPickerPresenterImpl(setsInteractor, cardsPreferences, logger);
    }

    @Provides
    CardsActivityPresenter providesCardsActivityPresenter(CardsInteractor cardsInteractor,
                                                          SavedCardsInteractor savedCardsInteractor,
                                                          DecksInteractor decksInteractor,
                                                          CardsPreferences cardsPreferences,
                                                          Logger logger) {
        return new CardsActivityPresenterImpl(cardsInteractor, savedCardsInteractor, decksInteractor, cardsPreferences, logger);
    }

    @Provides
    SearchPresenter providesSearchActivityPresenter(SetsInteractor setsInteratcor,
                                                    CardsInteractor cardsInteractor,
                                                    GeneralData generalData,
                                                    Logger logger) {
        return new SearchPresenterImpl(setsInteratcor, cardsInteractor, generalData, logger);
    }

    @Provides
    CardsLuckyPresenter providesCardsLuckyActivityPresenter(CardsInteractor cardsInteractor,
                                                            CardsPreferences cardsPreferences,
                                                            Logger logger) {
        return new CardsLuckyPresenterImpl(cardsInteractor, cardsPreferences, logger);
    }

    @Provides
    ReleaseNotePresenter providesReleaseNotePresenter(ReleaseNoteInteractor interactor) {
        return new ReleaseNotePresenter(interactor);
    }
}