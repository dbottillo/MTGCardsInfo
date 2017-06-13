package com.dbottillo.mtgsearchfree.interactors;

import android.support.annotation.NonNull;

import com.dbottillo.mtgsearchfree.model.CardsCollection;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorage;
import com.dbottillo.mtgsearchfree.model.storage.CardsStorageImpl;
import com.dbottillo.mtgsearchfree.util.Logger;

import java.util.List;

import io.reactivex.Observable;

public class CardsInteractorImpl implements CardsInteractor {

    private final CardsStorage storage;
    private final Logger logger;

    public CardsInteractorImpl(CardsStorage storage, Logger logger) {
        this.storage = storage;
        this.logger = logger;
        logger.d("created");
    }

    public Observable<CardsCollection> getLuckyCards(int howMany) {
        logger.d("get lucky cards");
        return Observable.just(storage.getLuckyCards(howMany));
    }

    public Observable<List<MTGCard>> getFavourites() {
        logger.d("get favourites");
        return Observable.just(storage.getFavourites());
    }

    public Observable<int[]> saveAsFavourite(MTGCard card) {
        logger.d("save as favourite");
        return Observable.just(storage.saveAsFavourite(card));
    }

    public Observable<int[]> removeFromFavourite(MTGCard card) {
        logger.d("remove from favourite");
        return Observable.just(storage.removeFromFavourite(card));
    }

    public Observable<CardsCollection> loadSet(MTGSet set) {
        logger.d("loadSet " + set.toString());
        return Observable.just(storage.load(set));
    }

    public Observable<int[]> loadIdFav() {
        logger.d("loadSet id fav");
        return Observable.just(storage.loadIdFav());
    }

    @Override
    public Observable<CardsCollection> doSearch(SearchParams searchParams) {
        logger.d("do search " + searchParams.toString());
        return Observable.just(storage.doSearch(searchParams));
    }

    @Override
    public Observable<MTGCard> loadCard(int multiverseid) {
        logger.d("loading card with multiverse id: " + multiverseid);
        return Observable.just(storage.loadCard(multiverseid));
    }

    @Override
    public Observable<MTGCard> loadOtherSideCard(MTGCard card) {
        logger.d("loading other side of card: " + card.toString());
        return Observable.just(storage.loadOtherSide(card));
    }
}

