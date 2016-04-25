package com.dbottillo.mtgsearchfree.model.storage;

import android.content.Context;
import android.net.Uri;

import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.database.CardsInfoDbHelper;
import com.dbottillo.mtgsearchfree.model.database.MTGDatabaseHelper;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.LOG;

import java.util.List;

public class DecksStorage {

    private CardsInfoDbHelper helper;
    private MTGDatabaseHelper mtgHelper;
    private Context context;

    public DecksStorage(Context context, CardsInfoDbHelper helper, MTGDatabaseHelper mtgHelper) {
        LOG.d("created");
        this.helper = helper;
        this.mtgHelper = mtgHelper;
        this.context = context;
    }

    public List<Deck> load() {
        LOG.d();
        return helper.getDecks();
    }

    public List<Deck> addDeck(String name) {
        LOG.d("add " + name);
        helper.addDecK(name);
        return load();
    }

    public List<Deck> deleteDeck(Deck deck) {
        LOG.d("delete " + deck);
        helper.deleteDeck(deck);
        return load();
    }

    public List<MTGCard> loadDeck(Deck deck) {
        LOG.d("loadSet " + deck);
        return helper.loadDeck(deck);
    }

    public List<MTGCard> editDeck(Deck deck, String name) {
        LOG.d("edit " + deck + " with " + name);
        helper.editDeck(deck, name);
        return loadDeck(deck);
    }

    public List<MTGCard> addCard(Deck deck, MTGCard card, int quantity) {
        LOG.d("add " + quantity + " " + card + " to " + deck);
        helper.addCard(deck, card, quantity);
        return loadDeck(deck);
    }

    public List<MTGCard> addCard(String name, MTGCard card, int quantity) {
        LOG.d("add " + quantity + " " + card + " with new deck name " + name);
        long deckId = helper.addDecK(name);
        helper.addCard(deckId, card, quantity);
        return helper.loadDeck(deckId);
    }

    public List<MTGCard> removeCard(Deck deck, MTGCard card) {
        LOG.d("remove " + card + " from " + deck);
        helper.addCard(deck, card, -1);
        return loadDeck(deck);
    }

    public List<MTGCard> removeAllCard(Deck deck, MTGCard card) {
        LOG.d("remove all " + card + " from " + deck);
        helper.removeAllCards(deck, card);
        return loadDeck(deck);
    }

    public List<Deck> importDeck(Uri uri) {
        CardsBucket bucket = FileUtil.readFileContent(context, uri);
        if ( bucket == null){
            return load();
        }
        return helper.addDeck(mtgHelper.getReadableDatabase(), bucket);
    }
}

