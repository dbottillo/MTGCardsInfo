package com.dbottillo.mtgsearchfree.view.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.PagerTabStrip
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import android.widget.RelativeLayout
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.bindView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.base.DBActivity
import com.dbottillo.mtgsearchfree.base.MTGApp
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter
import com.dbottillo.mtgsearchfree.resources.CardsBucket
import com.dbottillo.mtgsearchfree.resources.Deck
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.tracking.TrackingManager
import com.dbottillo.mtgsearchfree.util.MaterialWrapper
import com.dbottillo.mtgsearchfree.util.UIUtil
import com.dbottillo.mtgsearchfree.view.CardsView
import com.dbottillo.mtgsearchfree.view.adapters.CardsPagerAdapter
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment
import javax.inject.Inject

class CardsActivity : DBActivity(), CardsView, ViewPager.OnPageChangeListener {

    companion object {
        val KEY_SEARCH = "Search"
        val KEY_SET = "Set"
        val KEY_DECK = "Deck"
        val POSITION = "Position"
    }

    private var set: MTGSet? = null
    private var deck: Deck? = null
    private var search: String? = null
    private var startPosition: Int = 0
    private lateinit var idFavourites: IntArray
    private lateinit var bucket: CardsBucket
    private var favMenuItem: MenuItem? = null
    private var imageMenuItem: MenuItem? = null

    val viewPager: ViewPager by bindView(R.id.cards_view_pager)
    val pagerTabStrip: PagerTabStrip by bindView(R.id.cards_tab_strip)
    val fabButton: FloatingActionButton by bindView(R.id.card_add_to_deck)
    var adapter: CardsPagerAdapter? = null

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

    }

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

    private fun updateMenu() {
        var currentCard = adapter?.getItem(viewPager.currentItem)
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
        if (sharedPreferences.getBoolean(BasicFragment.PREF_SHOW_IMAGE, true)) {
            imageMenuItem?.isChecked = true
        } else {
            imageMenuItem?.isChecked = false
        }
    }

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
            var currentCard = adapter?.getItem(viewPager.currentItem)
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
            val showImage = sharedPreferences.getBoolean(BasicFragment.PREF_SHOW_IMAGE, true)
            val editor = sharedPreferences.edit()
            editor.putBoolean(BasicFragment.PREF_SHOW_IMAGE, !showImage)
            editor.apply()
            reloadAdapter()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reloadAdapter() {
        val showImage = sharedPreferences.getBoolean(BasicFragment.PREF_SHOW_IMAGE, true)
        adapter = CardsPagerAdapter(this, deck != null, showImage, bucket.cards)
        viewPager.adapter = adapter
        viewPager.currentItem = startPosition
        updateMenu()
    }

    private fun favClicked() {
        var currentCard = adapter?.getItem(viewPager.currentItem)!!
        if (idFavourites.contains(currentCard.multiVerseId)) {
            cardsPresenter.removeFromFavourite(currentCard)
        } else {
            cardsPresenter.saveAsFavourite(currentCard)
        }
    }

    override fun favIdLoaded(favourites: IntArray) {
        idFavourites = favourites

        if (adapter == null) {
            // first time needs to load cards
            if (set != null) {
                cardsPresenter.loadCards(set!!)
            } else if (search != null) {
                // load search
            } else {
                // something very bad happened here
                throw UnsupportedOperationException()
            }
        } else {
            updateMenu()
        }
    }

    override fun cardLoaded(bucket: CardsBucket) {
        this.bucket = bucket
        reloadAdapter()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            setFabScale(1.0f)
            updateMenu()
        }
    }

    override fun onPageSelected(position: Int) {
        updateMenu()
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
