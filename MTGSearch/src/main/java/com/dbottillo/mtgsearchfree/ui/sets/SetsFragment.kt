package com.dbottillo.mtgsearchfree.ui.sets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.ui.BaseHomeFragment
import com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorFragment
import com.dbottillo.mtgsearchfree.util.*
import com.dbottillo.mtgsearchfree.ui.lucky.CardLuckyActivity
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivity
import com.dbottillo.mtgsearchfree.ui.search.SearchActivity
import com.dbottillo.mtgsearchfree.ui.cards.OnCardListener
import com.dbottillo.mtgsearchfree.ui.decks.AddToDeckFragment
import com.dbottillo.mtgsearchfree.ui.DialogHelper
import com.dbottillo.mtgsearchfree.ui.views.MTGCardsView
import com.dbottillo.mtgsearchfree.ui.views.MTGLoader
import javax.inject.Inject

class SetsFragment : BaseHomeFragment(), SetsFragmentView, OnCardListener {

    @Inject
    lateinit var presenter: SetsFragmentPresenter

    lateinit var mtgCardsView: MTGCardsView
    lateinit var tooltip: ViewGroup
    lateinit var loader: MTGLoader

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mtgApp.uiGraph.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_sets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = view.findViewById<MTGLoader>(R.id.loader)
        view.findViewById<View>(R.id.action_search).setOnClickListener { startActivity(Intent(activity, SearchActivity::class.java)) }
        view.findViewById<View>(R.id.action_lucky).setOnClickListener { startActivity(Intent(activity, CardLuckyActivity::class.java)) }
        view.findViewById<View>(R.id.main_tooltip_close).setOnClickListener {
            generalData.setTooltipMainHide()
            AnimationUtil.animateHeight(tooltip, 0)
        }

        tooltip = view.findViewById<ViewGroup>(R.id.main_tooltip)
        mtgCardsView = view.findViewById<MTGCardsView>(R.id.cards)
        mtgCardsView.setEmptyString(R.string.empty_cards)

        if (generalData.isTooltipMainToShow) {
            MaterialWrapper.setElevation(tooltip, resources.getDimensionPixelSize(R.dimen.toolbar_elevation).toFloat())
        } else {
            UIUtil.setHeight(tooltip, 0)
        }

        setupHomeActivityScroll(recyclerView = mtgCardsView.listView)

        presenter.init(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.loadSets()
    }

    override fun getPageTrack(): String {
        return "/sets"
    }

    override fun getScrollViewId(): Int {
        return R.id.card_list
    }

    override fun getTitle(): String {
        return presenter.set()?.name ?: ""
    }

    override fun showSet(set: MTGSet, cardsCollection: CardsCollection) {
        toolbarRevealScrollHelper.updateTitle(set.name)
        mtgCardsView.loadCards(cardsCollection.list, this, set.name, R.drawable.ic_edit_grey, cardsCollection.filter, R.menu.card_option)
    }

    override fun onCardsViewTypeSelected() {
        presenter.toggleCardTypeViewPreference()
    }

    override fun onCardsSettingSelected() {
        val cardsConfigurator = CardsConfiguratorFragment()
        cardsConfigurator.show(dbActivity.supportFragmentManager, "cards_configurator")
        cardsConfigurator.listener = object : CardsConfiguratorFragment.CardsConfiguratorListener{
            override fun onConfigurationChange() {
                presenter.reloadSet()
            }
        }
    }

    override fun onCardSelected(card: MTGCard, position: Int) {
        TrackingManager.trackOpenCard(position)
        startActivity(CardsActivity.newInstance(context, presenter.set(), position, card))
    }

    override fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int) {
        LOG.d()
        when(menuItem.itemId){
            R.id.action_add_to_deck -> DialogHelper.open(dbActivity, "add_to_deck", AddToDeckFragment.newInstance(card))
            R.id.action_add_to_favourites -> {
                presenter.saveAsFavourite(card)
            }
        }
    }

    override fun showCardsGrid() {
        LOG.d()
        mtgCardsView.setGridOn()
    }

    override fun showCardsList() {
        LOG.d()
        mtgCardsView.setListOn()
    }

    override fun onCardsHeaderSelected() {
        val intent = Intent(activity, SetPickerActivity::class.java)
        startActivity(intent)
    }

    override fun hideLoading() {
        loader.visibility = View.GONE
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
    }
}