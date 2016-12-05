package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.view.CardView;

public interface CardPresenter {

    void loadOtherSideCard(MTGCard card);

    void init(CardView cardView);
}
