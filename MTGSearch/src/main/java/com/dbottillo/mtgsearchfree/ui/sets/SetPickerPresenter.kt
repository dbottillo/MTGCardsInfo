package com.dbottillo.mtgsearchfree.ui.sets

interface SetPickerPresenter {
    fun init(view: SetPickerView)
    fun loadSets()
    fun setSelected(pos: Int)
}
