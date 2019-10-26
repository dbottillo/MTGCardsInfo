package com.dbottillo.mtgsearchfree.saved

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.home.BaseHomeFragment
import com.dbottillo.mtgsearchfree.ui.cardsConfigurator.CardsConfiguratorFragment
import com.dbottillo.mtgsearchfree.ui.cards.OnCardListener
import com.dbottillo.mtgsearchfree.ui.views.MTGCardsView
import com.dbottillo.mtgsearchfree.ui.views.MTGLoader
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.util.gone
import com.dbottillo.mtgsearchfree.util.show
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SavedFragment : BaseHomeFragment(), SavedCardsView, OnCardListener {

    lateinit var loader: MTGLoader
    lateinit var mtgCardsView: MTGCardsView

    @Inject lateinit var savedCardsPresenter: SavedCardsPresenter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mtgCardsView = view.findViewById(R.id.cards)
        loader = view.findViewById(R.id.loader)

        setupHomeActivityScroll(viewRecycle = mtgCardsView.listView)

        mtgCardsView.setEmptyString(R.string.empty_saved)
        savedCardsPresenter.init(this)
    }

    override fun onResume() {
        super.onResume()
        savedCardsPresenter.load()
    }

    override fun onPause() {
        super.onPause()
        savedCardsPresenter.onPause()
    }

    override fun getScrollViewId() = R.id.card_list
    override fun getToolbarId() = R.id.toolbar
    override fun getToolbarTitleId() = R.id.toolbar_title

    override fun getPageTrack(): String {
        return "/saved"
    }

    override fun getTitle(): String {
        return resources.getString(R.string.action_saved)
    }

    override fun showError(message: String) {
        LOG.d()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        TrackingManager.trackSearchError(message)
    }

    override fun showError(exception: MTGException) {
        LOG.d()
        Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
        TrackingManager.trackSearchError(exception.message)
    }

    override fun render(uiModel: SavedCardsUiModel) {
        when (uiModel) {
            is SavedCardsUiModel.Loading -> {
                mtgCardsView.gone()
                loader.show()
            }
            is SavedCardsUiModel.Data -> {
                mtgCardsView.show()
                loader.gone()
                mtgCardsView.loadCards(uiModel.collection.list, this, getString(R.string.action_saved), uiModel.collection.filter, R.menu.card_saved_option)
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

    override fun onCardSelected(card: MTGCard, position: Int) {
        TrackingManager.trackOpenCard(position)
        navigator.openCardsSavedScreen(requireActivity(), position)
    }

    override fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int) {
        LOG.d()
        when (menuItem.itemId) {
            R.id.action_add_to_deck -> dbActivity.show("add_to_deck", navigator.newAddToDeckFragment(card))
            R.id.action_remove -> {
                savedCardsPresenter.removeFromFavourite(card)
                savedCardsPresenter.load()
            }
        }
    }

    override fun onCardsViewTypeSelected() {
        LOG.d()
        savedCardsPresenter.toggleCardTypeViewPreference()
    }

    override fun onCardsSettingSelected() {
        val cardsConfigurator = CardsConfiguratorFragment()
        cardsConfigurator.show(dbActivity.supportFragmentManager, "cards_configurator")
        cardsConfigurator.listener = object : CardsConfiguratorFragment.CardsConfiguratorListener {
            override fun onConfigurationChange() {
                savedCardsPresenter.load()
            }
        }
    }

    private fun openSearch() {
        LOG.d()
        navigator.openSearchScreen(activity!!)
    }

    @Suppress("EmptyFunctionBlock")
    override fun onTitleHeaderSelected() {
    }
}
