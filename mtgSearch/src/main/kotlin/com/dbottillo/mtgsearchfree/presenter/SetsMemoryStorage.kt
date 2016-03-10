package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.resources.MTGSet
import java.util.*

object SetsMemoryStorage {
    var init: Boolean = false
    var sets: List<MTGSet> = ArrayList()
}