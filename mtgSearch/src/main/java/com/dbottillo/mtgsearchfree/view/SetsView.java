package com.dbottillo.mtgsearchfree.view;

import com.dbottillo.mtgsearchfree.resources.MTGSet;

import java.util.List;

public interface SetsView extends BasicView {

    void setsLoaded(List<MTGSet> sets);
}