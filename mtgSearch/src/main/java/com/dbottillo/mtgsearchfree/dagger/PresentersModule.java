package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.interactors.SavedCardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.mapper.DeckMapper;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.model.storage.GeneralData;
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter;
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.CardPresenter;
import com.dbottillo.mtgsearchfree.presenter.CardPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.DecksPresenter;
import com.dbottillo.mtgsearchfree.presenter.DecksPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.MemoryStorage;
import com.dbottillo.mtgsearchfree.presenter.PlayerPresenter;
import com.dbottillo.mtgsearchfree.presenter.PlayerPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.RunnerFactory;
import com.dbottillo.mtgsearchfree.presenter.SetsPresenter;
import com.dbottillo.mtgsearchfree.presenter.SetsPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.saved.SavedCardsPresenter;
import com.dbottillo.mtgsearchfree.ui.saved.SavedCardsPresenterImpl;
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragmentPresenter;
import com.dbottillo.mtgsearchfree.ui.sets.SetsFragmentPresenterImpl;
import com.dbottillo.mtgsearchfree.util.Logger;
import com.dbottillo.mtgsearchfree.view.helpers.CardsHelper;

import dagger.Module;
import dagger.Provides;

@Module
public class PresentersModule {

    @Provides
    CardFilterPresenter provideCardFilterPresenter(CardFilterInteractor interactor,
                                                   RunnerFactory factory,
                                                   MemoryStorage memoryStorage, Logger logger) {
        return new CardFilterPresenterImpl(interactor, factory, memoryStorage, logger);
    }

    @Provides
    CardsPresenter provideCardsPresenter(CardsInteractor interactor,
                                         DeckMapper deckMapper,
                                         GeneralData generalData,
                                         RunnerFactory factory,
                                         MemoryStorage memoryStorage,
                                         Logger logger) {
        return new CardsPresenterImpl(interactor, deckMapper, generalData, factory, memoryStorage, logger);
    }

    @Provides
    SetsPresenter provideSetsPresenter(SetsInteractor interactor,
                                       RunnerFactory factory,
                                       CardsPreferences cardsPreferences,
                                       MemoryStorage memoryStorage, Logger logger) {
        return new SetsPresenterImpl(interactor, factory, cardsPreferences, memoryStorage, logger);
    }

    @Provides
    PlayerPresenter providePlayerPresenter(PlayerInteractor interactor,
                                           RunnerFactory factory, Logger logger) {
        return new PlayerPresenterImpl(interactor, factory, logger);
    }

    @Provides
    DecksPresenter provideDecksPresenter(DecksInteractor interactor,
                                         DeckMapper deckMapper,
                                         RunnerFactory factory, Logger logger) {
        return new DecksPresenterImpl(interactor, deckMapper, factory, logger);
    }

    @Provides
    CardsHelper provideCardsHelper(CardsPreferences cardsPreferences) {
        return new CardsHelper(cardsPreferences);
    }

    @Provides
    CardPresenter provideCardPresenter(CardsInteractor interactor, Logger logger, RunnerFactory runnerFactory){
        return new CardPresenterImpl(interactor, logger, runnerFactory);
    }

    @Provides
    SavedCardsPresenter provideSavedCardsPresenter(SavedCardsInteractor interactor,
                                                   GeneralData generalData,
                                                   Logger logger,
                                                   RunnerFactory runnerFactory){
        return new SavedCardsPresenterImpl(interactor, runnerFactory, generalData, logger);
    }

    @Provides
    SetsFragmentPresenter providesSetsFragmentPresenter(SetsInteractor setsInteractor,
                                                        CardsInteractor cardsInteractor,
                                                        CardsPreferences cardsPreferences,
                                                        RunnerFactory runnerFactory,
                                                        GeneralData generalData,
                                                        Logger logger){
        return new SetsFragmentPresenterImpl(setsInteractor, cardsInteractor, cardsPreferences, runnerFactory, generalData, logger);
    }
}