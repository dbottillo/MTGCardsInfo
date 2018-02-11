package com.dbottillo.mtgsearchfree.ui.lucky

import android.net.Uri
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.BaseCardsView

interface CardsLuckyView : BaseCardsView{
    fun showCard(card: MTGCard, showImage: Boolean)
    fun preFetchCardImage(card: MTGCard)
    fun shareUri(uri: Uri)
    fun showError(message: String)
}