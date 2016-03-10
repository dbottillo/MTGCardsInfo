package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.resources.MTGCard
import java.util.*

object CardsMemoryStorage {
    var currentKey: String = ""
    var cards: List<MTGCard> = ArrayList()

    fun isValid(key: String): Boolean {
        if (currentKey.equals(key) && cards.size > 0) {
            return true;
        }
        return false;
    }

    fun update(newKey: String, newCards: List<MTGCard>) {
        currentKey = newKey
        cards = newCards
    }
}