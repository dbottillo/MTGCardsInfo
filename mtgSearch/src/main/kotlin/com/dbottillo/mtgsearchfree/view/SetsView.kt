package com.dbottillo.mtgsearchfree.view

import com.dbottillo.mtgsearchfree.resources.MTGSet

interface SetsView : BasicView {

    fun setsLoaded(sets: List<MTGSet>)
}