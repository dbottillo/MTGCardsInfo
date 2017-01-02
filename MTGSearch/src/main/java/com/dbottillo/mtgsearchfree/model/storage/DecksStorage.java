package com.dbottillo.mtgsearchfree.model.storage;

import android.net.Uri;

import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode;
import com.dbottillo.mtgsearchfree.exceptions.MTGException;
import com.dbottillo.mtgsearchfree.model.CardsBucket;
import com.dbottillo.mtgsearchfree.model.Deck;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource;
import com.dbottillo.mtgsearchfree.util.FileUtil;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.Logger;

import java.util.List;

import javax.inject.Inject;

public class DecksStorage {

    private final DeckDataSource deckDataSource;
    private final FileUtil fileUtil;
    private final Logger logger;

    @Inject
    public DecksStorage(FileUtil fileUtil, DeckDataSource deckDataSource, Logger logger) {
        this.logger = logger;
        this.deckDataSource = deckDataSource;
        this.fileUtil = fileUtil;
        logger.d("created");
    }

    public List<Deck> load() {
        logger.d();
        return deckDataSource.getDecks();
    }

    public List<Deck> addDeck(String name) {
        logger.d("add " + name);
        deckDataSource.addDeck(name);
        return load();
    }

    public List<Deck> deleteDeck(Deck deck) {
        logger.d("delete " + deck);
        deckDataSource.deleteDeck(deck);
        return load();
    }

    public List<MTGCard> loadDeck(Deck deck) {
        logger.d("loadSet " + deck);
        return deckDataSource.getCards(deck);
    }

    public List<MTGCard> editDeck(Deck deck, String name) {
        logger.d("edit " + deck + " with " + name);
        deckDataSource.renameDeck(deck.getId(), name);
        return loadDeck(deck);
    }

    public List<MTGCard> addCard(Deck deck, MTGCard card, int quantity) {
        logger.d("add " + quantity + " " + card + " to " + deck);
        deckDataSource.addCardToDeck(deck.getId(), card, quantity);
        return loadDeck(deck);
    }

    public List<MTGCard> addCard(String name, MTGCard card, int quantity) {
        logger.d("add " + quantity + " " + card + " with new deck name " + name);
        long deckId = deckDataSource.addDeck(name);
        deckDataSource.addCardToDeck(deckId, card, quantity);
        return deckDataSource.getCards(deckId);
    }

    public List<MTGCard> removeCard(Deck deck, MTGCard card) {
        logger.d("remove " + card + " from " + deck);
        deckDataSource.addCardToDeck(deck.getId(), card, -1);
        return loadDeck(deck);
    }

    public List<MTGCard> removeAllCard(Deck deck, MTGCard card) {
        logger.d("remove all " + card + " from " + deck);
        deckDataSource.removeCardFromDeck(deck.getId(), card);
        return loadDeck(deck);
    }

    public List<Deck> importDeck(Uri uri) throws MTGException {
        CardsBucket bucket;
        try {
            bucket = fileUtil.readFileContent(uri);
        } catch (Exception e) {
            throw new MTGException(ExceptionCode.DECK_NOT_IMPORTED, "file not valid");
        }
        if (bucket == null) {
            throw new MTGException(ExceptionCode.DECK_NOT_IMPORTED, "bucket null");
        }
        deckDataSource.addDeck(bucket);
        return deckDataSource.getDecks();
    }

    public List<MTGCard> moveCardFromSideboard(Deck deck, MTGCard card, int quantity) {
        logger.d("move [" + quantity + ']' + card + " from sideboard of" + deck);
        deckDataSource.moveCardFromSideBoard(deck.getId(), card, quantity);
        return loadDeck(deck);
    }

    public List<MTGCard> moveCardToSideboard(Deck deck, MTGCard card, int quantity) {
        logger.d("move [" + quantity + ']' + card + " to sideboard of" + deck);
        deckDataSource.moveCardToSideBoard(deck.getId(), card, quantity);
        return loadDeck(deck);
    }
}

