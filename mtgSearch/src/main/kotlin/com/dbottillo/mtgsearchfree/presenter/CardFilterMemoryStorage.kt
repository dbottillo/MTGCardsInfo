package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.resources.CardFilter

class CardFilterMemoryStorage private constructor() {

    private object Holder {
        val INSTANCE = CardFilterMemoryStorage()
    }

    companion object {
        val instance: CardFilterMemoryStorage by lazy { Holder.INSTANCE }
    }

    var filter: CardFilter? = null
}