package com.dbottillo.mtgsearchfree.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.base.DBActivity
import com.dbottillo.mtgsearchfree.cards.FullScreenImageActivity
import com.dbottillo.mtgsearchfree.cards.MTGCardFragment
import com.dbottillo.mtgsearchfree.cards.MTGCardsFragment
import com.dbottillo.mtgsearchfree.communication.DataManager
import com.dbottillo.mtgsearchfree.communication.events.SavedCardsEvent
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.util.MaterialWrapper
import java.util.*

class CardsActivity : DBActivity(), MTGCardFragment.CardConnector {

    private val savedCards = ArrayList<MTGCard>()

    internal var deck: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)

        setupToolbar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        MaterialWrapper.setElevation(toolbar, 0f)

        /*cardsFragment = supportFragmentManager.findFragmentById(R.id.container) as MTGCardsFragment

        if (cardsFragment == null) {
            title = intent.getStringExtra(MTGCardsFragment.TITLE)
            deck = intent.getBooleanExtra(MTGCardsFragment.DECK, false)
            cardsFragment = MTGCardsFragment.newInstance(intent.getIntExtra(MTGCardsFragment.POSITION, 0),
                    title, deck)
            supportFragmentManager.beginTransaction().replace(R.id.container, cardsFragment).commit()
        }*/
    }

    /*override fun onResume() {
        super.onResume()
        DataManager.execute(DataManager.TASK.SAVED_CARDS, false)
    }
*/
    override fun getPageTrack(): String {
        if (deck) {
            return "/deck"
        }
        return "/cards"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FULLSCREEN_CODE && resultCode == Activity.RESULT_OK) {
            cardsFragment!!.goTo(data.getIntExtra(MTGCardsFragment.POSITION, 0))
        }
    }*/

    override fun isCardSaved(card: MTGCard): Boolean {
        var isSaved = false
        for (savedCard in savedCards) {
            if (savedCard.multiVerseId == card.multiVerseId) {
                isSaved = true
                break
            }
        }
        return isSaved
    }

    override fun saveCard(card: MTGCard) {
        DataManager.execute(DataManager.TASK.SAVE_CARD, card)
        savedCards.add(card)
        invalidateOptionsMenu()
    }

    override fun removeCard(card: MTGCard) {
        DataManager.execute(DataManager.TASK.UN_SAVE_CARD, card)
        for (savedCard in savedCards) {
            if (savedCard.multiVerseId == card.multiVerseId) {
                savedCards.remove(savedCard)
                break
            }
        }
        invalidateOptionsMenu()
    }

    override fun tapOnImage(position: Int) {
        openFullScreen(position)
        TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_CARD, "fullscreen", "tap_on_image")
    }

    fun openFullScreen(currentItem: Int) {
        val fullScreen = Intent(this, FullScreenImageActivity::class.java)
        fullScreen.putExtra(MTGCardsFragment.POSITION, currentItem)
        fullScreen.putExtra(MTGCardsFragment.TITLE, title)
        fullScreen.putExtra(MTGCardsFragment.DECK, deck)
        startActivityForResult(fullScreen, CardsActivity.FULLSCREEN_CODE)
    }

    fun onEventMainThread(event: SavedCardsEvent) {
        if (event.isError) {
            Toast.makeText(this, R.string.error_favourites, Toast.LENGTH_SHORT).show()
            TrackingHelper.getInstance(this).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "saved-cards", event.errorMessage)
        } else {
            savedCards.clear()
            for (card in event.result) {
                savedCards.add(card)
            }
            invalidateOptionsMenu()
        }
        bus.removeStickyEvent(event)
    }

    companion object {

        val FULLSCREEN_CODE = 100
    }
}
