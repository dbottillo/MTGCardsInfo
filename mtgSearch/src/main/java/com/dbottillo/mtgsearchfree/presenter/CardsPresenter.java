package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.resources.MTGSet;
import com.dbottillo.mtgsearchfree.view.CardsView;

public interface CardsPresenter extends BasicPresenter {

    void init(CardsView view);

    void loadCards(MTGSet set);

    void loadIdFavourites();

    void saveAsFavourite(MTGCard card);

    void removeFromFavourite(MTGCard card);

    void getLuckyCards(int howMany);
}