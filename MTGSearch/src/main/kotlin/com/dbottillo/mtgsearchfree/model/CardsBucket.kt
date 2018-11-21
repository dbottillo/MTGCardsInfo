package com.dbottillo.mtgsearchfree.model

data class CardsBucket(
    val key: String,
    var cards: List<MTGCard> = listOf()
) {

    constructor(set: MTGSet, cards: List<MTGCard>) : this(set.name, cards)

    fun isValid(otherKey: String): Boolean {
        return key == otherKey
    }
}
