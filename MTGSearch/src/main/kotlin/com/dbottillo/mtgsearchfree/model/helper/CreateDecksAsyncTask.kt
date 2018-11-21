package com.dbottillo.mtgsearchfree.model.helper

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.dbottillo.mtgsearchfree.model.database.CardDataSource
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource
import com.dbottillo.mtgsearchfree.model.database.MTGCardDataSource
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper
import com.google.gson.Gson
import java.lang.ref.WeakReference
import java.util.Random

/*
    This class is used only on debug to generate random decks
 */
class CreateDecksAsyncTask(context: Context) : AsyncTask<String, Void, ArrayList<Any>>() {

    private val context: WeakReference<Context> = WeakReference(context)

    override fun doInBackground(vararg params: String): ArrayList<Any> {
        val result = ArrayList<Any>()

        context.get()?.let {
            val databaseHelper = MTGDatabaseHelper(it)
            val cardsInfoDbHelper = CardsInfoDbHelper(it)
            val db = cardsInfoDbHelper.writableDatabase
            val cardDataSource = CardDataSource(cardsInfoDbHelper.writableDatabase, Gson())
            val mtgCardDataSource = MTGCardDataSource(databaseHelper.readableDatabase, cardDataSource)

            val deckDataSource = DeckDataSource(db, cardDataSource, mtgCardDataSource)
            deckDataSource.deleteAllDecks(db)

            val r = Random()
            for (i in 0..98) {
                val deck = deckDataSource.addDeck("Deck " + i)
                val cards = mtgCardDataSource.getRandomCard(30)
                for (card in cards) {
                    val quantity = r.nextInt(4) + 1
                    // LOG.e("adding " + quantity + " " + card.getName() + " to " + deck);
                    card.isSideboard = quantity == 1
                    deckDataSource.addCardToDeckWithoutCheck(deck, card, quantity)
                }
            }

            db.close()
        }

        return result
    }

    override fun onPostExecute(result: ArrayList<Any>) {
        context.get()?.let {
            Toast.makeText(it, "finished", Toast.LENGTH_SHORT).show()
        }
    }
}
