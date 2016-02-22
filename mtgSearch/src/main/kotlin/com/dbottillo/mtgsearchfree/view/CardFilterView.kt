package com.dbottillo.mtgsearchfree.view

import com.dbottillo.mtgsearchfree.resources.CardFilter

interface CardFilterView {

    fun filterLoaded(filter: CardFilter)
}