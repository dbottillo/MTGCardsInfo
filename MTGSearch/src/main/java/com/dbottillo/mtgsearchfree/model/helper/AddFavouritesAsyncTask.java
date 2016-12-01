package com.dbottillo.mtgsearchfree.model.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.FavouritesDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.util.ArrayList;
import java.util.List;

/*
    This class is used only on debug to generate random favourites cards.
 */
public class AddFavouritesAsyncTask extends AsyncTask<String, Void, ArrayList<Object>> {

    private boolean error = false;
    private Context context;

    public AddFavouritesAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<Object> doInBackground(String... params) {
        ArrayList<Object> result = new ArrayList<>();

        MTGDatabaseHelper databaseHelper = new MTGDatabaseHelper(context);
        CardsInfoDbHelper cardsInfoDbHelper = CardsInfoDbHelper.getInstance(context);
        MTGCardDataSource mtgCardDataSource = new MTGCardDataSource(databaseHelper);

        FavouritesDataSource favouritesDataSource = new FavouritesDataSource(cardsInfoDbHelper.getWritableDatabase());
        favouritesDataSource.clear();
        List<MTGCard> cards = mtgCardDataSource.getRandomCard(600);
        for (MTGCard card : cards) {
            favouritesDataSource.saveFavourites(card);
        }

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Object> result) {
        if (error) {
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "finished", Toast.LENGTH_SHORT).show();
        }
    }

}
