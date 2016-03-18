package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;

import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.resources.MTGCard;
import com.dbottillo.mtgsearchfree.resources.MTGSet;

import java.util.ArrayList;

public class CardsStorage {

    private Context context;

    public CardsStorage(Context context) {
        this.context = context;
    }

    public ArrayList<MTGCard> load(MTGSet set) {
        MTGDatabaseHelper helper = new MTGDatabaseHelper(context);
        return helper.getSet(set);
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
        MTGDatabaseHelper helper = new MTGDatabaseHelper(context);
        return helper.getRandomCard(howMany);
    }

}
