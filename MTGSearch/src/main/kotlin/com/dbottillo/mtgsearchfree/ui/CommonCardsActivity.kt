package com.dbottillo.mtgsearchfree.ui

import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.graphics.Bitmap
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.util.getBitmap
import javax.inject.Inject

abstract class CommonCardsActivity : BasicActivity() {

    protected var favMenuItem: MenuItem? = null
    protected var imageMenuItem: MenuItem? = null

    protected abstract val currentCard: MTGCard?

    @Inject
    lateinit var cardsPreferences: CardsPreferences

    protected abstract fun favClicked()

    protected abstract fun toggleImage(show: Boolean)

    protected abstract fun syncMenu()

    protected abstract fun shareImage(bitmap: Bitmap)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        LOG.e("onCreateOptionsMenu")
        menuInflater.inflate(R.menu.cards, menu)
        favMenuItem = menu.findItem(R.id.action_fav)
        imageMenuItem = menu.findItem(R.id.action_image)
        menu.findItem(R.id.action_fullscreen_image).isVisible = false
        syncMenu()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_fav -> {
                favClicked()
                true
            }
            R.id.action_share -> {
                currentCard?.also {
                    TrackingManager.trackShareCard(it.name)
                    val i = Intent(Intent.ACTION_SEND)
                    i.type = "text/plain"
                    i.putExtra(Intent.EXTRA_SUBJECT, it.name)
                    val url = "http://gatherer.wizards.com/Pages/Card/Details.aspx?multiverseid=" + it.multiVerseId
                    i.putExtra(Intent.EXTRA_TEXT, url)
                    startActivity(Intent.createChooser(i, getString(R.string.share_card)))
                }
                true
            }
            R.id.action_share_artwork -> {
                currentCard?.let {
                    it.getBitmap(this) { bitmap ->
                        shareImage(bitmap)
                    }
                }
                true
            }
            R.id.action_image -> {
                val showImage = cardsPreferences.showImage()
                cardsPreferences.setShowImage(!showImage)
                toggleImage(!showImage)
                syncMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun shareUriArtwork(name: String, uri: Uri) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.type = "image/*"
        startActivity(Intent.createChooser(shareIntent, name))
    }
}