package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.resources.CardFilter

object CardFilterMemoryStorage {
    var init: Boolean = false
    var filter: CardFilter = CardFilter()
}