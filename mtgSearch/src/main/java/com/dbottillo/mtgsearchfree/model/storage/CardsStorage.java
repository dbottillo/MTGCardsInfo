package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.database.CardDataSource;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CardsStorage {

    private CardsInfoDbHelper cardsInfoDbHelper;
    private MTGCardDataSource mtgCardDataSource;

    public CardsStorage(MTGCardDataSource mtgCardDataSource, CardsInfoDbHelper cardsInfoDbHelper) {
        LOG.d("created");
        this.mtgCardDataSource = mtgCardDataSource;
        this.cardsInfoDbHelper = cardsInfoDbHelper;
    }

    public List<MTGCard> load(MTGSet set) {
        LOG.d("loadSet " + set);
        return mtgCardDataSource.getSet(set);
    }

    public int[] saveAsFavourite(MTGCard card) {
        LOG.d("save as fav " + card);
        cardsInfoDbHelper.saveFavourite(card);
        return loadIdFav();
    }

    public int[] loadIdFav() {
        LOG.d();
        List<MTGCard> cards = cardsInfoDbHelper.loadFav(false);
        int[] result = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            result[i] = cards.get(i).getMultiVerseId();
        }
        return result;
    }

    public int[] removeFromFavourite(MTGCard card) {
        LOG.d("remove as fav " + card);
        cardsInfoDbHelper.removeFavourite(card);
        return loadIdFav();
    }

    public List<MTGCard> getLuckyCards(int howMany) {
        LOG.d(howMany + " lucky cards requested");
        return mtgCardDataSource.getRandomCard(howMany);
    }

    public List<MTGCard> getFavourites() {
        LOG.d();
        return cardsInfoDbHelper.loadFav(true);
    }

    public List<MTGCard> loadDeck(Deck deck) {
        LOG.d("loadSet " + deck);
        return cardsInfoDbHelper.loadDeck(deck);
    }

    public List<MTGCard> doSearch(SearchParams searchParams) {
        LOG.d("do search " + searchParams);
        List<MTGCard> result = mtgCardDataSource.searchCards(searchParams);

        Collections.sort(result, new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                MTGCard card = (MTGCard) o1;
                MTGCard card2 = (MTGCard) o2;
                return card.compareTo(card2);
            }
        });
        return result;
    }
}
