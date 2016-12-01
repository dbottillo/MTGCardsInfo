package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.model.database.FavouritesDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CardsStorage {

    private MTGCardDataSource mtgCardDataSource;
    private DeckDataSource deckDataSource;
    private FavouritesDataSource favouritesDataSource;

    public CardsStorage(MTGCardDataSource mtgCardDataSource, DeckDataSource deckDataSource, FavouritesDataSource favouritesDataSource) {
        LOG.d("created");
        this.deckDataSource = deckDataSource;
        this.favouritesDataSource = favouritesDataSource;
        this.mtgCardDataSource = mtgCardDataSource;
    }

    public List<MTGCard> load(MTGSet set) {
        LOG.d("loadSet " + set);
        return mtgCardDataSource.getSet(set);
    }

    public int[] saveAsFavourite(MTGCard card) {
        LOG.d("save as fav " + card);
        favouritesDataSource.saveFavourites(card);
        return loadIdFav();
    }

    public int[] loadIdFav() {
        LOG.d();
        List<MTGCard> cards = favouritesDataSource.getCards(false);
        int[] result = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            result[i] = cards.get(i).getMultiVerseId();
        }
        return result;
    }

    public int[] removeFromFavourite(MTGCard card) {
        LOG.d("remove as fav " + card);
        favouritesDataSource.removeFavourites(card);
        return loadIdFav();
    }

    public List<MTGCard> getLuckyCards(int howMany) {
        LOG.d(howMany + " lucky cards requested");
        return mtgCardDataSource.getRandomCard(howMany);
    }

    public List<MTGCard> getFavourites() {
        LOG.d();
        return favouritesDataSource.getCards(true);
    }

    public List<MTGCard> loadDeck(Deck deck) {
        LOG.d("loadSet " + deck);
        return deckDataSource.getCards(deck);
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
