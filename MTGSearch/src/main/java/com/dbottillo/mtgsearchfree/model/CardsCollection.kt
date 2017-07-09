package com.dbottillo.mtgsearchfree.model

open class CardsCollection(val list: List<MTGCard>,
                           val filter: CardFilter? = null,
                           val isDeck: Boolean = false){

    fun isEmpty(): Boolean {
        return list.isEmpty()
    }
}