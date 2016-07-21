package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.view.SetsView;

public interface SetsPresenter {

    void init(SetsView view);

    void loadSets();

    void setSelected(int position);

    int getCurrentSetPosition();

}