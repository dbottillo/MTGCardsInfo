package com.dbottillo.mtgsearchfree.presenter;

import com.dbottillo.mtgsearchfree.model.CardsBucket;

public final class CardsMemoryStorage {

    public CardsMemoryStorage() {

    }

    private CardsBucket bucket = null;

    private int[] favourites = null;

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

    public boolean isBucketType(String value) {
        return !(bucket == null || !bucket.getKey().equals(value));
    }

    public void invalidate() {
        bucket = null;
    }
}