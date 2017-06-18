package com.dbottillo.mtgsearchfree.ui.search

import android.animation.ArgbEvaluator
import android.annotation.TargetApi
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.Toast
import butterknife.BindView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.model.SearchParams
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivity
import com.dbottillo.mtgsearchfree.ui.cardsCoonfigurator.CardsConfiguratorFragment
import com.dbottillo.mtgsearchfree.util.*
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.view.adapters.OnCardListener
import com.dbottillo.mtgsearchfree.ui.decks.AddToDeckFragment
import com.dbottillo.mtgsearchfree.view.helpers.DialogHelper
import com.dbottillo.mtgsearchfree.view.views.MTGCardsView
import com.dbottillo.mtgsearchfree.view.views.MTGSearchView
import javax.inject.Inject

class SearchActivity : BasicActivity(), View.OnClickListener, SearchActivityView, OnCardListener {

    lateinit var newSearch: ImageButton
    lateinit var scrollView: ScrollView
    lateinit var mtgCardsView: MTGCardsView
    lateinit var searchView: MTGSearchView

    @BindView(R.id.close_button)
    lateinit var closeButton: ImageButton

    internal var newSearchAnimation: AnimationDrawable? = null
    internal var argbEvaluator = ArgbEvaluator()

    internal var sizeBig = 0
    internal var searchOpen = false

    @Inject
    lateinit var presenter: SearchPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        newSearch = findViewById(R.id.action_search) as ImageButton
        scrollView = findViewById(R.id.search_scroll_view) as ScrollView
        mtgCardsView = findViewById(R.id.cards_list_view) as MTGCardsView
        searchView = findViewById(R.id.search_view) as MTGSearchView
        closeButton = findViewById(R.id.close_button) as ImageButton

        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_close)
        toolbar.setTitle(R.string.action_search)
        mtgCardsView.setEmptyString(R.string.empty_search)
        closeButton.setOnClickListener {
            LOG.e("closeButton ")
            newSearch.callOnClick()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState != null) {
            searchOpen = savedInstanceState.getBoolean(SEARCH_OPEN)
            scrollView.setBackgroundColor(savedInstanceState.getInt(BG_COLOR_SCROLLVIEW))
            MaterialWrapper.setElevation(toolbar, savedInstanceState.getFloat(TOOLBAR_ELEVATION))
            MaterialWrapper.setStatusBarColor(this, if (searchOpen) resources.getColor(R.color.color_accent_dark) else resources.getColor(R.color.status_bar))
            closeButton.setAlpha(if (searchOpen) 1 else 0)
        } else {
            MaterialWrapper.setElevation(toolbar, 0f)
        }

        scrollView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                sizeBig = scrollView.height
                UIUtil.setMarginTop(mtgCardsView, sizeBig)
                if (searchOpen) {
                    UIUtil.setHeight(scrollView, 0)
                    UIUtil.setMarginTop(mtgCardsView, 0)
                    mtgCardsView.visibility = View.VISIBLE
                }
                scrollView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        toolbar.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                sizeToolbar = toolbar.height
                toolbar.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        setupScrollviewListener()

        argbEvaluator = ArgbEvaluator()

        newSearch.setOnClickListener(this)
        newSearch.setBackgroundResource(R.drawable.anim_search_icon)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            newSearch.elevation = 6.0f // TODO: pre-lollipop version
        }

        mtgApp.uiGraph.inject(this)

        if (savedInstanceState != null) {
            val searchParams = savedInstanceState.getParcelable<SearchParams>(SEARCH_PARAMS)
            if (searchParams != null) {
                doSearch(searchParams)
            }
        }

        presenter.init(this)
        presenter.loadSet()
    }

    private fun setupScrollviewListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupScrollViewListenerM()
        } else {
            scrollView.viewTreeObserver.addOnScrollChangedListener { computeScrollChanged(scrollView.scrollY) }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun setupScrollViewListenerM() {
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ -> computeScrollChanged(scrollY) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SEARCH_OPEN, searchOpen)
        val color = argbEvaluator.evaluate(if (scrollView.scrollY < 400) scrollView.scrollY.toFloat() / 400.toFloat() else 1f, resources.getColor(R.color.color_primary), resources.getColor(R.color.color_primary_slightly_dark)) as Int
        outState.putInt(BG_COLOR_SCROLLVIEW, color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outState.putFloat(TOOLBAR_ELEVATION, toolbar.elevation)
        }
        outState.putParcelable(SEARCH_PARAMS, searchView.searchParams)
    }

    override fun getPageTrack(): String {
        return "/search_main"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doSearch(searchParams: SearchParams) {
        LOG.d()
        TrackingManager.trackSearch(searchParams)
        presenter.doSearch(searchParams)
        hideIme()
    }

    override fun onClick(v: View) {
        LOG.d()
        var searchParams: SearchParams? = null
        if (!searchOpen) {
            searchParams = searchView.searchParams
            if (!searchParams.isValid) {
                Toast.makeText(this, getString(R.string.minimum_search), Toast.LENGTH_SHORT).show()
                return
            }
        }
        val backgroundInterpolator = AnimationUtil.createLinearInterpolator()
        val startColor: Int
        val endColor: Int
        if (!searchOpen) {
            newSearch.setBackgroundResource(R.drawable.anim_search_icon)
            backgroundInterpolator.fromValue(sizeBig.toFloat()).toValue(0f)
            startColor = resources.getColor(R.color.status_bar)
            endColor = resources.getColor(R.color.color_accent_dark)
        } else {
            newSearch.setBackgroundResource(R.drawable.anim_search_icon_reverse)
            backgroundInterpolator.fromValue(0f).toValue(sizeBig.toFloat())
            startColor = resources.getColor(R.color.color_accent_dark)
            endColor = resources.getColor(R.color.status_bar)
        }
        newSearchAnimation = newSearch.background as AnimationDrawable
        newSearchAnimation?.start()
        val anim = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                super.applyTransformation(interpolatedTime, t)
                val `val` = backgroundInterpolator.getInterpolation(interpolatedTime).toInt()
                UIUtil.setHeight(scrollView, `val`)
                UIUtil.setMarginTop(mtgCardsView, `val`)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val color = argbEvaluator.evaluate(interpolatedTime, startColor, endColor) as Int
                    this@SearchActivity.window.statusBarColor = color
                }
            }
        }
        val finalSearchParams = searchParams
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                closeButton.visibility = View.VISIBLE
                if (!searchOpen) {
                    mtgCardsView.visibility = View.VISIBLE
                    closeButton.animate().setDuration(100).alpha(1f).start()
                    toolbar.animate().setDuration(100).translationY((-sizeToolbar).toFloat()).start()
                } else {
                    closeButton.animate().setDuration(100).alpha(0f).start()
                    toolbar.animate().setDuration(100).translationY(0f).start()
                }
            }

            override fun onAnimationEnd(animation: Animation) {
                if (searchOpen) {
                    closeButton.visibility = View.GONE
                    mtgCardsView.visibility = View.GONE
                } else {
                    closeButton.visibility = View.VISIBLE
                    finalSearchParams?.let {
                        doSearch(it)
                    }
                }
                searchOpen = !searchOpen
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        anim.duration = 200
        scrollView.startAnimation(anim)
    }

    private fun computeScrollChanged(amount: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.elevation = if (amount < 200) 9 * (amount.toFloat() / 200.toFloat()) else 9f
        }
        val color = argbEvaluator.evaluate(if (amount < 400) amount.toFloat() / 400.toFloat() else 1f, resources.getColor(R.color.color_primary), resources.getColor(R.color.color_primary_slightly_dark)) as Int
        scrollView.setBackgroundColor(color)
    }

    override fun onBackPressed() {
        if (searchOpen) {
            newSearch.callOnClick()
        } else {
            super.onBackPressed()
        }
    }

    override fun setLoaded(data: List<MTGSet>) {
        LOG.d()
        searchView.refreshSets(data)
    }

    override fun showSearch(data: CardsCollection) {
        mtgCardsView.loadCards(data.list, this, R.string.search_result, -1)
    }

    override fun showCardsGrid() {
        mtgCardsView.setGridOn()
    }

    override fun showCardsList() {
        mtgCardsView.setListOn()
    }

    override fun onCardsHeaderSelected() {

    }

    override fun onCardsViewTypeSelected() {
        presenter.toggleCardTypeViewPreference()
    }

    override fun onCardsSettingSelected() {
        val fragment = CardsConfiguratorFragment(false, true)
        fragment.listener = object : CardsConfiguratorFragment.CardsConfiguratorListener {
            override fun onConfigurationChange() {
                presenter.doSearch(searchView.searchParams)
            }
        }
        fragment.show(supportFragmentManager, "cards_configurator")
    }

    override fun onCardSelected(card: MTGCard, position: Int, view: View) {
        LOG.d()
        val intent = CardsActivity.newInstance(this, searchView.searchParams, position, null)
        startActivity(intent)
    }

    override fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int) {
        if (menuItem.itemId == R.id.action_add_to_deck) {
            DialogHelper.open(this, "add_to_deck", AddToDeckFragment.newInstance(card))

        } else if (menuItem.itemId == R.id.action_add_to_favourites) {
            presenter.saveAsFavourite(card)
        }
    }

    companion object {

        private val SEARCH_OPEN = "searchOpen"
        private val BG_COLOR_SCROLLVIEW = "bgColorScrollview"
        private val TOOLBAR_ELEVATION = "toolbarElevation"
        private val SEARCH_PARAMS = "searchParams"
    }
}
