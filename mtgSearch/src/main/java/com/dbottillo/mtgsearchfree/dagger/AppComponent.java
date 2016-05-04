package com.dbottillo.mtgsearchfree.dagger;

import android.content.SharedPreferences;

import com.dbottillo.mtgsearchfree.MTGApp;
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
import com.dbottillo.mtgsearchfree.model.storage.CardFilterStorage;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;
import com.dbottillo.mtgsearchfree.model.storage.DecksStorage;
import com.dbottillo.mtgsearchfree.model.storage.PlayersStorage;
import com.dbottillo.mtgsearchfree.model.storage.SetsStorage;
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.CardsPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.DecksPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.PlayerPresenterImpl;
import com.dbottillo.mtgsearchfree.presenter.RxDoubleWrapper;
import com.dbottillo.mtgsearchfree.presenter.RxWrapper;
import com.dbottillo.mtgsearchfree.presenter.SetsPresenterImpl;
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity;

import java.util.List;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AndroidModule.class, DataModule.class, InteractorsModule.class})
public interface AppComponent {

    CardFilterInteractor getCardFilterInteractor();

    CardsInteractor getCardsInteractor();

    CardFilterStorage getCardFilterStorage();

    CardsStorage getCardsStorage();

    SetsInteractor getSetsInteractor();

    SetsStorage getSetsStorage();

    PlayerInteractor getPlayerInteractor();

    PlayersStorage getPlayerStorage();

    DecksInteractor getDecksInteractor();

    DecksStorage getDecksStorage();

    RxWrapper<List<MTGSet>> getRxWrapperListMTGSet();

    RxWrapper<CardFilter> getRxWrapperCardFilter();

    RxWrapper<List<Player>> getRxWrapperListPlayer();

    RxWrapper<List<MTGCard>> getRxWrapperListCard();

    RxWrapper<List<Deck>> getRxWrapperListDeck();

    RxDoubleWrapper<List<MTGCard>, DeckBucket> getRxWrapperDeckBucket();

    RxWrapper<int[]> getRxWrapperFav();

    DeckMapper getDeckMapper();

    SharedPreferences getSharedPreferences();

    void inject(MTGApp app);

    void inject(BasicActivity mainActivity);

    void inject(SetsPresenterImpl setsPresenter);

    void inject(CardFilterPresenterImpl cardFilterPresenter);

    void inject(PlayerPresenterImpl playerPresenter);

    void inject(DecksPresenterImpl decksPresenter);

    void inject(CardsPresenterImpl cardsPresenter);
}
