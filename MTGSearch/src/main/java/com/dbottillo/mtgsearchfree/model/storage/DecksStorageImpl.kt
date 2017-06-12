package com.dbottillo.mtgsearchfree.model.storage

import android.net.Uri

import com.dbottillo.mtgsearchfree.exceptions.ExceptionCode
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.mapper.DeckMapper
import com.dbottillo.mtgsearchfree.model.CardsBucket
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.database.DeckDataSource
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.Logger

import javax.inject.Inject

class DecksStorageImpl @Inject
constructor(private val fileUtil: FileUtil,
            private val deckDataSource: DeckDataSource,
            private val deckMapper: DeckMapper,
            private val logger: Logger) : DecksStorage {

    init {
        logger.d("created")
    }

    override fun load(): List<Deck> {
        logger.d()
        return deckDataSource.decks
    }

    override fun addDeck(name: String): List<Deck> {
        logger.d("add " + name)
        deckDataSource.addDeck(name)
        return load()
    }

    override fun deleteDeck(deck: Deck): List<Deck> {
        logger.d("delete " + deck)
        deckDataSource.deleteDeck(deck)
        return load()
    }

    override fun loadDeck(deck: Deck): CardsCollection {
        logger.d("loadDeck " + deck)
        val cards = deckDataSource.getCards(deck)
        return CardsCollection(deckMapper.order(cards), isDeck = true)
    }

    override fun editDeck(deck: Deck, name: String): CardsCollection {
        logger.d("edit $deck with $name")
        deckDataSource.renameDeck(deck.id, name)
        return loadDeck(deck)
    }

    override fun addCard(deck: Deck, card: MTGCard, quantity: Int): CardsCollection {
        logger.d("add $quantity $card to $deck")
        deckDataSource.addCardToDeck(deck.id, card, quantity)
        return loadDeck(deck)
    }

    override fun addCard(name: String, card: MTGCard, quantity: Int): CardsCollection {
        logger.d("add $quantity $card with new deck name $name")
        val deckId = deckDataSource.addDeck(name)
        deckDataSource.addCardToDeck(deckId, card, quantity)
        return CardsCollection(deckDataSource.getCards(deckId), isDeck = true)
    }

    override fun removeCard(deck: Deck, card: MTGCard): CardsCollection {
        logger.d("remove $card from $deck")
        deckDataSource.addCardToDeck(deck.id, card, -1)
        return loadDeck(deck)
    }

    override fun removeAllCard(deck: Deck, card: MTGCard): CardsCollection {
        logger.d("remove all $card from $deck")
        deckDataSource.removeCardFromDeck(deck.id, card)
        return loadDeck(deck)
    }

    @Throws(MTGException::class)
    override fun importDeck(uri: Uri): List<Deck> {
        val bucket: CardsBucket?
        try {
            bucket = fileUtil.readFileContent(uri)
        } catch (e: Exception) {
            throw MTGException(ExceptionCode.DECK_NOT_IMPORTED, "file not valid")
        }

        if (bucket == null) {
            throw MTGException(ExceptionCode.DECK_NOT_IMPORTED, "bucket null")
        }
        deckDataSource.addDeck(bucket)
        return deckDataSource.decks
    }

    override fun moveCardFromSideboard(deck: Deck, card: MTGCard, quantity: Int): CardsCollection {
        logger.d("move [$quantity]$card from sideboard of$deck")
        deckDataSource.moveCardFromSideBoard(deck.id, card, quantity)
        return loadDeck(deck)
    }

    override fun moveCardToSideboard(deck: Deck, card: MTGCard, quantity: Int): CardsCollection {
        logger.d("move [$quantity]$card to sideboard of$deck")
        deckDataSource.moveCardToSideBoard(deck.id, card, quantity)
        return loadDeck(deck)
    }
}

