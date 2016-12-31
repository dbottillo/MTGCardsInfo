package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.view.CardsView;

public interface CardsPresenter extends BasicPresenter {

    void init(CardsView view);

    void loadCards(MTGSet set);

    void loadIdFavourites();

    void saveAsFavourite(MTGCard card, boolean reload);

    void removeFromFavourite(MTGCard card, boolean reload);

    void getLuckyCards(int howMany);

    void loadFavourites();

    void loadDeck(Deck deck);

    void doSearch(SearchParams searchParams);

    void loadCardTypePreference();

    void toggleCardTypeViewPreference();

}