package com.dbottillo.mtgsearchfree.presenter

import com.dbottillo.mtgsearchfree.view.SetsView

interface SetsPresenter {

    fun init(view: SetsView)

    fun loadSets()
}