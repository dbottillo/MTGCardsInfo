package com.dbottillo.mtgsearchfree.util

import com.dbottillo.mtgsearchfree.model.Deck
import java.util.*

fun Deck.toDeckName(): String {
    return this.name.replace("\\s+".toRegex(), "").toLowerCase(Locale.getDefault())
}