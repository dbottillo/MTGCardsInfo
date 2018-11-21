package com.dbottillo.mtgsearchfree.ui.sets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.ui.BaseHomeFragment
import com.dbottillo.mtgsearchfree.ui.cards.OnCardListener
import com.dbottillo.mtgsearchfree.ui.cards.startCardsActivity
import com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorFragment
import com.dbottillo.mtgsearchfree.ui.decks.addToDeck.AddToDeckFragment
import com.dbottillo.mtgsearchfree.ui.lucky.CardLuckyActivity
import com.dbottillo.mtgsearchfree.ui.search.SearchActivity
import com.dbottillo.mtgsearchfree.ui.views.MTGCardsView
import com.dbottillo.mtgsearchfree.ui.views.MTGLoader
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.util.show
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SetsFragment : BaseHomeFragment(), SetsFragmentView, OnCardListener {

    @Inject
    lateinit var presenter: SetsFragmentPresenter

    private lateinit var mtgCardsView: MTGCardsView
    private lateinit var loader: MTGLoader

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_sets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loader = view.findViewById(R.id.loader)
        view.findViewById<View>(R.id.action_search).setOnClickListener { startActivity(Intent(activity, SearchActivity::class.java)) }
        view.findViewById<View>(R.id.action_lucky).setOnClickListener { startActivity(Intent(activity, CardLuckyActivity::class.java)) }
        view.findViewById<View>(R.id.change_set).setOnClickListener {
            onTitleHeaderSelected()
        }

        mtgCardsView = view.findViewById(R.id.cards)
        mtgCardsView.setEmptyString(R.string.empty_cards)

        setupHomeActivityScroll(viewRecycle = mtgCardsView.listView)

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
        mtgCardsView.loadCards(cardsCollection.list, this, set.name, cardsCollection.filter, R.menu.card_option)
    }

    override fun onCardsViewTypeSelected() {
        presenter.toggleCardTypeViewPreference()
    }

    override fun onCardsSettingSelected() {
        val cardsConfigurator = CardsConfiguratorFragment()
        cardsConfigurator.show(dbActivity.supportFragmentManager, "cards_configurator")
        cardsConfigurator.listener = object : CardsConfiguratorFragment.CardsConfiguratorListener {
            override fun onConfigurationChange() {
                presenter.reloadSet()
            }
        }
    }

    override fun onCardSelected(card: MTGCard, position: Int) {
        TrackingManager.trackOpenCard(position)
        startActivity(presenter.set()?.let { context?.startCardsActivity(it, position) })
    }

    override fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int) {
        LOG.d()
        when (menuItem.itemId) {
            R.id.action_add_to_deck -> dbActivity.show("add_to_deck", AddToDeckFragment.newInstance(card))
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

    override fun onTitleHeaderSelected() {
        startActivity(Intent(activity, SetPickerActivity::class.java))
    }

    override fun hideLoading() {
        loader.visibility = View.GONE
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
    }
}