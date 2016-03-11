package com.dbottillo.mtgsearchfree.view.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.PagerTabStrip
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.widget.RelativeLayout
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.bindView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.base.DBActivity
import com.dbottillo.mtgsearchfree.base.MTGApp
import com.dbottillo.mtgsearchfree.communication.events.SavedCardsEvent
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter
import com.dbottillo.mtgsearchfree.resources.CardsBucket
import com.dbottillo.mtgsearchfree.resources.Deck
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.util.MaterialWrapper
import com.dbottillo.mtgsearchfree.util.UIUtil
import com.dbottillo.mtgsearchfree.view.CardsView
import com.dbottillo.mtgsearchfree.view.adapters.CardsPagerAdapter
import java.util.*
import javax.inject.Inject

class CardsActivity : DBActivity(), CardsView, ViewPager.OnPageChangeListener {

    companion object {
        val FULLSCREEN_CODE = 100
        val CARD_POSITION = "Search"
        val KEY_SEARCH = "Search"
        val KEY_SET = "Set"
        val KEY_DECK = "Deck"
        val POSITION = "Position"
    }

    private var set: MTGSet? = null
    private var deck: Deck? = null
    private var search: String? = null
    private var startPosition: Int = 0
    private var idFavourites: IntArray? = null

    val viewPager: ViewPager by bindView(R.id.cards_view_pager)
    val pagerTabStrip: PagerTabStrip by bindView(R.id.cards_tab_strip)
    val fabButton: FloatingActionButton by bindView(R.id.card_add_to_deck)

    private val savedCards = ArrayList<MTGCard>()

    @Inject lateinit var cardsPresenter: CardsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cards)

        ButterKnife.bind(this)

        setupView()

        MTGApp.Companion.dataGraph.inject(this)
        cardsPresenter.init(this)

        if (intent != null) {
            if (intent.hasExtra(KEY_SET)) {
                set = intent.getParcelableExtra(KEY_SET)
                supportActionBar?.title = set?.name

            } else if (intent.hasExtra(KEY_SEARCH)) {
                search = intent.getStringExtra(KEY_SEARCH)

            } else if (intent.hasExtra(KEY_DECK)) {
                deck = intent.getParcelableExtra(KEY_DECK)
            }
        }
        startPosition = intent.getIntExtra(POSITION, 0)

        cardsPresenter.loadIdFavourites()

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

    fun setupView() {
        setupToolbar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        MaterialWrapper.setElevation(toolbar, 0f)

        pagerTabStrip.tabIndicatorColor = resources.getColor(R.color.white)
        pagerTabStrip.setBackgroundColor(resources.getColor(R.color.color_primary))
        pagerTabStrip.setTextColor(resources.getColor(R.color.white))
        var par = fabButton.layoutParams as RelativeLayout.LayoutParams
        if (isPortrait) {
            par.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        } else {
            par.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
            par.rightMargin = UIUtil.dpToPx(this, 16)
        }
        fabButton.layoutParams = par
        viewPager.addOnPageChangeListener(this)
    }

    override fun getPageTrack(): String {
        if (deck != null) {
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

    override fun favIdLoaded(favourites: IntArray) {
        idFavourites = favourites
        if (set != null) {
            cardsPresenter.loadCards(set!!)
        } else if (search != null) {
            // load search
        } else {
            // something very bad happened here
            throw UnsupportedOperationException()
        }
    }

    override fun cardLoaded(bucket: CardsBucket) {
        var adapter = CardsPagerAdapter(this, deck != null, bucket.cards)
        viewPager.adapter = adapter
        viewPager.currentItem = startPosition
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FULLSCREEN_CODE && resultCode == Activity.RESULT_OK) {
            cardsFragment!!.goTo(data.getIntExtra(MTGCardsFragment.POSITION, 0))
        }
    }*/

    /*override fun isCardSaved(card: MTGCard): Boolean {
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
    }*/

    /*fun openFullScreen(currentItem: Int) {
        val fullScreen = Intent(this, FullScreenImageActivity::class.java)
        fullScreen.putExtra(MTGCardsFragment.POSITION, currentItem)
        fullScreen.putExtra(MTGCardsFragment.TITLE, title)
        fullScreen.putExtra(MTGCardsFragment.DECK, deck)
        startActivityForResult(fullScreen, CardsActivity.FULLSCREEN_CODE)
    }*/

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

    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setFabScale(1.0f)
        }
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (positionOffset.toDouble() != 0.0) {
            if (positionOffset < 0.5) {
                setFabScale(1.0f - positionOffset)
            } else {
                setFabScale(positionOffset)
            }
        }
    }

    fun setFabScale(value: Float) {
        fabButton.scaleX = value
        fabButton.scaleY = value
    }

}
