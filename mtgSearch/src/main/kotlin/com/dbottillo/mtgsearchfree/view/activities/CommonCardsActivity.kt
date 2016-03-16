package com.dbottillo.mtgsearchfree.view.activities

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment

abstract class CommonCardsActivity : BasicActivity() {

    private var favMenuItem: MenuItem? = null
    private var imageMenuItem: MenuItem? = null
    protected lateinit var idFavourites: IntArray

    abstract fun getCurrentCard(): MTGCard?
    abstract fun favClicked()
    abstract fun toggleImage(show: Boolean)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cards, menu)
        favMenuItem = menu.findItem(R.id.action_fav)
        imageMenuItem = menu.findItem(R.id.action_image)
        menu.findItem(R.id.action_fullscreen_image)?.isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        if (id == R.id.action_fav) {
            favClicked()
            return true
        }
        if (id == R.id.action_share) {
            var currentCard = getCurrentCard()
            TrackingManager.trackShareCard(currentCard);
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, currentCard?.name)
            val url = "http://gatherer.wizards.com/Pages/Card/Details.aspx?multiverseid=" + currentCard?.multiVerseId
            i.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(Intent.createChooser(i, getString(R.string.share_card)))
            return true
        }
        if (id == R.id.action_image) {
            val showImage = getSharedPreferences().getBoolean(BasicFragment.PREF_SHOW_IMAGE, true)
            val editor = getSharedPreferences().edit()
            editor.putBoolean(BasicFragment.PREF_SHOW_IMAGE, !showImage)
            editor.apply()
            toggleImage(!showImage)
            updateMenu()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun updateMenu() {
        var currentCard = getCurrentCard()
        if (currentCard != null && currentCard.multiVerseId.toInt() > 0) {
            favMenuItem?.isVisible = true
            if (idFavourites.contains(currentCard.multiVerseId)) {
                favMenuItem?.title = getString(R.string.favourite_remove)
                favMenuItem?.setIcon(R.drawable.ab_star_colored)
            } else {
                favMenuItem?.title = getString(R.string.favourite_add)
                favMenuItem?.setIcon(R.drawable.ab_star)
            }
        } else {
            favMenuItem?.isVisible = false
        }
        if (getSharedPreferences().getBoolean(BasicFragment.PREF_SHOW_IMAGE, true)) {
            imageMenuItem?.isChecked = true
        } else {
            imageMenuItem?.isChecked = false
        }
    }

}