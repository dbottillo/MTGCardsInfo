package com.dbottillo.mtgsearchfree.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.database.FavouritesDataSource;
import com.dbottillo.mtgsearchfree.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.model.MTGCard;

import java.util.ArrayList;
import java.util.List;

public class AddFavouritesAsyncTask extends AsyncTask<String, Void, ArrayList<Object>> {

    private boolean error = false;
    private Context context;

    public AddFavouritesAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<Object> doInBackground(String... params) {
        ArrayList<Object> result = new ArrayList<>();

        MTGDatabaseHelper databaseHelper = MTGDatabaseHelper.getInstance(context);
        CardsInfoDbHelper cardsInfoDbHelper = CardsInfoDbHelper.getInstance(context);

        FavouritesDataSource.clear(cardsInfoDbHelper.getWritableDatabase());
        List<MTGCard> cards = databaseHelper.getRandomCard(600);
        for (MTGCard card : cards) {
            FavouritesDataSource.saveFavourites(cardsInfoDbHelper.getWritableDatabase(), card);
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
