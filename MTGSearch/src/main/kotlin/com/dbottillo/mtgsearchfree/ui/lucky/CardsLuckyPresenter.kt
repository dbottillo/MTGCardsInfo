package com.dbottillo.mtgsearchfree.ui.lucky

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.dbottillo.mtgsearchfree.model.MTGCard

interface CardsLuckyPresenter {
    fun init(view: CardsLuckyView, bundle: Bundle?, intent: Intent?)
    fun showNextCard()
    fun updateMenu()
    fun onSaveInstanceState(outState: Bundle)
    fun saveOrRemoveCard()
    fun shareImage(bitmap: Bitmap)
}