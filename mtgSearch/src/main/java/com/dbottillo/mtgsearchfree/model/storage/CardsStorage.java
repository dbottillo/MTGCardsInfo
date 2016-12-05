package com.dbottillo.mtgsearchfree.model.storage;

import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.model.database.FavouritesDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.util.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CardsStorage {

    private final MTGCardDataSource mtgCardDataSource;
    private final DeckDataSource deckDataSource;
    private final FavouritesDataSource favouritesDataSource;
    private final Logger logger;

    public CardsStorage(MTGCardDataSource mtgCardDataSource, DeckDataSource deckDataSource, FavouritesDataSource favouritesDataSource, Logger logger) {
        this.logger = logger;
        logger.d("created");
        this.deckDataSource = deckDataSource;
        this.favouritesDataSource = favouritesDataSource;
        this.mtgCardDataSource = mtgCardDataSource;
    }

    public List<MTGCard> load(MTGSet set) {
        logger.d("loadSet " + set);
        return mtgCardDataSource.getSet(set);
    }

    public int[] saveAsFavourite(MTGCard card) {
        logger.d("save as fav " + card);
        favouritesDataSource.saveFavourites(card);
        return loadIdFav();
    }

    public int[] loadIdFav() {
        logger.d();
        List<MTGCard> cards = favouritesDataSource.getCards(false);
        int[] result = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            result[i] = cards.get(i).getMultiVerseId();
        }
        return result;
    }

    public int[] removeFromFavourite(MTGCard card) {
        logger.d("remove as fav " + card);
        favouritesDataSource.removeFavourites(card);
        return loadIdFav();
    }

    public List<MTGCard> getLuckyCards(int howMany) {
        logger.d(howMany + " lucky cards requested");
        return mtgCardDataSource.getRandomCard(howMany);
    }

    public List<MTGCard> getFavourites() {
        logger.d();
        return favouritesDataSource.getCards(true);
    }

    public List<MTGCard> loadDeck(Deck deck) {
        logger.d("loadSet " + deck);
        return deckDataSource.getCards(deck);
    }

    public List<MTGCard> doSearch(SearchParams searchParams) {
        logger.d("do search " + searchParams);
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

    public MTGCard loadCard(int multiverseId) {
        logger.d("do search with multiverse: " + multiverseId);
        return mtgCardDataSource.searchCard(multiverseId);
    }

    public MTGCard loadOtherSide(MTGCard card) {
        logger.d("do search other side card " + card.toString());
        if (card.getNames() == null) {
            return card;
        }
        if (card.getNames().size() < 2) {
            return card;
        }
        String name = card.getNames().get(0);
        if (name.equalsIgnoreCase(card.getName())) {
            name = card.getNames().get(1);
        }
        return mtgCardDataSource.searchCard(name);
    }
}
