package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.MTGSet;

import java.util.List;

public class MemoryStorage {

    private int[] favourites = null;
    private List<MTGSet> sets;

    public MemoryStorage() {

    }

    public int[] getFavourites() {
        return favourites;
    }

    public void setFavourites(int[] favourites) {
        this.favourites = favourites;
    }

    public List<MTGSet> getSets() {
        return sets;
    }

    public void setSets(List<MTGSet> sets) {
        this.sets = sets;
    }
}