package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.ui.BasicView;

public interface CardView extends BasicView {

    void otherSideCardLoaded(MTGCard card);
}