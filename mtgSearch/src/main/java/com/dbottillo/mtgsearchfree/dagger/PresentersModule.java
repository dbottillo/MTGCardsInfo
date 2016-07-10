package com.dbottillo.mtgsearchfree.dagger;

import com.dbottillo.mtgsearchfree.interactors.CardFilterInteractor;
import com.dbottillo.mtgsearchfree.interactors.CardsInteractor;
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor;
import com.dbottillo.mtgsearchfree.interactors.PlayerInteractor;
import com.dbottillo.mtgsearchfree.interactors.SetsInteractor;
import com.dbottillo.mtgsearchfree.mapper.DeckMapper;
import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.DeckBucket;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.Player;
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences;
import com.dbottillo.mtgsearchfree.model.storage.GeneralPreferences;
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter;
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.MemoryStorage;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.DecksPresenter;
import com.dbottillo.mtgsearchfree.presenter.DecksPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.PlayerPresenter;
import com.dbottillo.mtgsearchfree.presenter.PlayerPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.RxDoubleWrapper;
import com.dbottillo.mtgsearchfree.presenter.RxWrapper;
import com.dbottillo.mtgsearchfree.presenter.SetsPresenter;
import com.dbottillo.mtgsearchfree.presenter.SetsPresenterImpl;
import com.dbottillo.mtgsearchfree.view.helpers.CardsHelper;

import java.util.List;

import dagger.Module;
import dagger.Provides;

@Module
public class PresentersModule {

    @Provides
    CardFilterPresenter provideCardFilterPresenter(CardFilterInteractor interactor, RxWrapper<CardFilter> wrapper) {
        return new CardFilterPresenterImpl(interactor, wrapper);
    }

    @Provides
    CardsPresenter provideCardsPresenter(CardsInteractor interactor, DeckMapper deckMapper, GeneralPreferences generalPreferences,
                                         RxWrapper<List<MTGCard>> cardsWrapper,
                                         RxDoubleWrapper<List<MTGCard>, DeckBucket> deckWrapper,
                                         RxWrapper<int[]> favWrapper, MemoryStorage memoryStorage) {
        return new CardsPresenterImpl(interactor, deckMapper, generalPreferences, cardsWrapper, deckWrapper, favWrapper, memoryStorage);
    }

    @Provides
    SetsPresenter provideSetsPresenter(SetsInteractor interactor, RxWrapper<List<MTGSet>> wrapper,
                                       CardsPreferences cardsPreferences, MemoryStorage memoryStorage) {
        return new SetsPresenterImpl(interactor, wrapper, cardsPreferences, memoryStorage);
    }

    @Provides
    PlayerPresenter providePlayerPresenter(PlayerInteractor interactor, RxWrapper<List<Player>> rxWrapper) {
        return new PlayerPresenterImpl(interactor, rxWrapper);
    }

    @Provides
    DecksPresenter provideDecksPresenter(DecksInteractor interactor, DeckMapper deckMapper,
                                         RxWrapper<List<Deck>> deckWrapper,
                                         RxDoubleWrapper<List<MTGCard>, DeckBucket> cardWrapper) {
        return new DecksPresenterImpl(interactor, deckMapper, deckWrapper, cardWrapper);
    }

    @Provides
    CardsHelper provideCardsHelper(CardsPreferences cardsPreferences) {
        return new CardsHelper(cardsPreferences);
    }

}