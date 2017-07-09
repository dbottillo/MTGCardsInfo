package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.ui.BasicView;

import java.util.List;

public interface SetsView extends BasicView {

    void setsLoaded(List<MTGSet> sets);
}