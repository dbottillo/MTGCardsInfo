package com.dbottillo.helper;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dbottillo.database.CardsDatabaseHelper;
import com.dbottillo.database.DeckDataSource;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;
import java.util.Random;

public class CreateDecksAsyncTask extends AsyncTask<String, Void, ArrayList<Object>> {

    private boolean error = false;
    private Context context;

    public CreateDecksAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<Object> doInBackground(String... params) {
        ArrayList<Object> result = new ArrayList<Object>();

        CardsDatabaseHelper databaseHelper = new CardsDatabaseHelper(context);

        DeckDataSource deckDataSource = new DeckDataSource(context);
        deckDataSource.open();
        deckDataSource.deleteAllDecks();

        for (int i = 0; i < 99; i++) {
            long deck = deckDataSource.addDeck("Deck " + i);
            Cursor cursor = databaseHelper.getRandomCard(30);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    MTGCard card = MTGCard.createCardFromCursor(cursor);
                    Random r = new Random();
                    int quantity = r.nextInt(4) + 1;
                    LOG.e("adding " + quantity + " " + card.getName() + " to " + deck);
                    deckDataSource.addCardToDeckWithoutCheck(deck, card, quantity, quantity == 1);
                    cursor.moveToNext();
                }
            }
            cursor.close();
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
