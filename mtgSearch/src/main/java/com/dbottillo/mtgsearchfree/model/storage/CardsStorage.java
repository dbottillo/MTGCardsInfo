package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;

import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource;
import com.dbottillo.mtgsearchfree.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.MTGSet;
import com.dbottillo.mtgsearchfree.model.SearchParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CardsStorage {

    private Context context;

    public CardsStorage(Context context) {
        this.context = context;
    }

    public ArrayList<MTGCard> load(MTGSet set) {
        return MTGDatabaseHelper.getInstance(context).getSet(set);
    }

    public int[] saveAsFavourite(MTGCard card) {
        FavouritesDataSource.saveFavourites(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), card);
        return loadIdFav();
    }

    public int[] loadIdFav() {
        CardsInfoDbHelper helper = CardsInfoDbHelper.getInstance(context);
        ArrayList<MTGCard> cards = FavouritesDataSource.getCards(helper.getReadableDatabase(), false);
        int[] result = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            result[i] = cards.get(i).getMultiVerseId();
        }
        return result;
    }

    public int[] removeFromFavourite(MTGCard card) {
        FavouritesDataSource.removeFavourites(CardsInfoDbHelper.getInstance(context).getWritableDatabase(), card);
        return loadIdFav();
    }

    public ArrayList<MTGCard> getLuckyCards(int howMany) {
        return MTGDatabaseHelper.getInstance(context).getRandomCard(howMany);
    }

    public ArrayList<MTGCard> getFavourites() {
        return FavouritesDataSource.getCards(CardsInfoDbHelper.getInstance(context).getReadableDatabase(), true);
    }

    public ArrayList<MTGCard> loadDeck(Deck deck) {
        return DeckDataSource.getCards(CardsInfoDbHelper.getInstance(context).getReadableDatabase(), deck);
    }

    public ArrayList<MTGCard> doSearch(SearchParams searchParams) {
        ArrayList<MTGCard> result = MTGCardDataSource.searchCards(MTGDatabaseHelper.getInstance(context).getReadableDatabase(), searchParams);

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
