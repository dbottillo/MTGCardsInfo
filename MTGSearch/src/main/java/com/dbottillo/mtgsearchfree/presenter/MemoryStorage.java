package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.MTGSet;

import java.util.List;

public final class MemoryStorage {

    public MemoryStorage() {

    }

    private CardsBucket bucket = null;
    private int[] favourites = null;
    private boolean init;
    private List<MTGSet> sets;

    public CardsBucket getBucket() {
        return bucket;
    }

    public void setBucket(CardsBucket bucket) {
        this.bucket = bucket;
    }

    public int[] getFavourites() {
        return favourites;
    }

    public void setFavourites(int[] favourites) {
        this.favourites = favourites;
    }

    boolean isBucketType(String value) {
        return !(bucket == null || !bucket.getKey().equals(value));
    }

    void invalidateBucket() {
        bucket = null;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public List<MTGSet> getSets() {
        return sets;
    }

    public void setSets(List<MTGSet> sets) {
        this.sets = sets;
    }
}