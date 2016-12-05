package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.model.MTGCard;

public interface CardView extends BasicView {

    void otherSideCardLoaded(MTGCard card);
}