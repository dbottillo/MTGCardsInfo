package com.dbottillo.mtgsearchfree.resources

import java.util.*

class CardsBucket {

    var set: MTGSet
    var cards: ArrayList<MTGCard>

    constructor(set: MTGSet, cards: ArrayList<MTGCard>) {
        this.set = set
        this.cards = cards
    }

    constructor(search: String, cards: ArrayList<MTGCard>) {
        this.set = MTGSet(-1)
        this.cards = cards
        this.set.name = search
    }

    fun getKey(): String {
        return set.name
    }

    fun isValid(key: String?): Boolean {
        return true
    }
}
