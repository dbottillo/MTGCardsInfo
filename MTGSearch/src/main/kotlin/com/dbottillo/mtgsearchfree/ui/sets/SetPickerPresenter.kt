package com.dbottillo.mtgsearchfree.ui.sets

import com.dbottillo.mtgsearchfree.model.MTGSet

interface SetPickerPresenter {
    fun init(view: SetPickerView)
    fun loadSets()
    fun setSelected(set: MTGSet)
    fun search(text: String)
    fun onDestroy()
}
