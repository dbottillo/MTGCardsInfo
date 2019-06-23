package com.dbottillo.mtgsearchfree.ui

import com.dbottillo.mtgsearchfree.exceptions.MTGException

interface BasicView {
    fun showError(message: String)
    fun showError(exception: MTGException)
}
