package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.view.CardFilterView;

public interface CardFilterPresenter {

    void init(CardFilterView view);

    void loadFilter();

    void update(CardFilter.TYPE type, boolean on);

}