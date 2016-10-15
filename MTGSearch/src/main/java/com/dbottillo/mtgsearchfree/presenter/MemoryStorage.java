package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.CardFilter;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.List;

public class MemoryStorage {

    private int[] favourites = null;
    private List<MTGSet> sets;
    private CardFilter filter;

    public MemoryStorage() {
        LOG.d("created");
    }

    public int[] getFavourites() {
        if (favourites == null) {
            return null;
        }
        return favourites.clone();
    }

    public void setFavourites(int[] favourites) {
        this.favourites = favourites.clone();
    }

    public List<MTGSet> getSets() {
        return sets;
    }

    public void setSets(List<MTGSet> sets) {
        this.sets = sets;
    }

    public CardFilter getFilter() {
        return filter;
    }

    public void setFilter(CardFilter filter) {
        this.filter = filter;
    }
}