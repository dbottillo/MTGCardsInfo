package com.dbottillo.mtgsearchfree.model.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.database.CardDataSource;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
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

        MTGDatabaseHelper databaseHelper = new MTGDatabaseHelper(context);
        CardsInfoDbHelper cardsInfoDbHelper = new CardsInfoDbHelper(context);
        SQLiteDatabase db = cardsInfoDbHelper.getWritableDatabase();
        CardDataSource cardDataSource = new CardDataSource(cardsInfoDbHelper.getWritableDatabase(), new Gson());
        MTGCardDataSource mtgCardDataSource = new MTGCardDataSource(databaseHelper.getReadableDatabase(), cardDataSource);

        DeckDataSource deckDataSource = new DeckDataSource(db, cardDataSource, mtgCardDataSource);
        deckDataSource.deleteAllDecks(db);

        for (int i = 0; i < 99; i++) {
            long deck = deckDataSource.addDeck("Deck " + i);
            List<MTGCard> cards = mtgCardDataSource.getRandomCard(30);
            for (MTGCard card : cards) {
                Random r = new Random();
                int quantity = r.nextInt(4) + 1;
                //LOG.e("adding " + quantity + " " + card.getName() + " to " + deck);
                card.setSideboard(quantity == 1);
                deckDataSource.addCardToDeckWithoutCheck(deck, card, quantity);
            }
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
