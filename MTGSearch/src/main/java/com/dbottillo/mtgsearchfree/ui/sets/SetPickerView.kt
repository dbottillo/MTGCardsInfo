package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.model.MTGSet

interface SetPickerView {
    fun showSets(sets: List<MTGSet>, selectedPos: Int)
    fun close()
}
