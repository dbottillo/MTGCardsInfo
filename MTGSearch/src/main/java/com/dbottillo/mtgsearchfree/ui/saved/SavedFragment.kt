package com.dbottillo.mtgsearchfree.ui.saved

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import butterknife.BindView
import butterknife.OnClick
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.*
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter
import com.dbottillo.mtgsearchfree.ui.BaseHomeFragment
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.PermissionUtil
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.view.CardFilterView
import com.dbottillo.mtgsearchfree.view.CardsView
import com.dbottillo.mtgsearchfree.view.activities.CardsActivity
import com.dbottillo.mtgsearchfree.view.activities.SearchActivity
import com.dbottillo.mtgsearchfree.view.adapters.OnCardListener
import com.dbottillo.mtgsearchfree.view.fragments.AddToDeckFragment
import com.dbottillo.mtgsearchfree.view.helpers.CardsHelper
import com.dbottillo.mtgsearchfree.view.helpers.DialogHelper
import com.dbottillo.mtgsearchfree.view.views.MTGCardsView
import javax.inject.Inject

class SavedFragment : BaseHomeFragment(), SavedCardsView, OnCardListener {

    lateinit var mtgCardsView: MTGCardsView
    lateinit var emptyContainer : LinearLayout

    @Inject
    lateinit var savedCardsPresenter: SavedCardsPresenter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_saved, container, false)
        mtgApp.uiGraph.inject(this)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mtgCardsView = view.findViewById(R.id.cards) as MTGCardsView
        emptyContainer = view.findViewById(R.id.empty_saved_cards_container) as LinearLayout
        view.findViewById(R.id.empty_cards_action).setOnClickListener{ openSearch() }

        savedCardsPresenter.init(this)
        savedCardsPresenter.load()
    }

    override fun getScrollViewId(): Int {
        return R.id.card_list
    }

    override fun getPageTrack(): String {
        return "/saved"
    }

    override fun getTitle(): String {
        return context.getString(R.string.action_saved)
    }

    override fun showError(message: String?) {
        LOG.d()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        TrackingManager.trackSearchError(message)
    }

    override fun showError(exception: MTGException?) {
        LOG.d()
        Toast.makeText(activity, exception?.message, Toast.LENGTH_SHORT).show()
        TrackingManager.trackSearchError(exception?.message)
    }

    override fun showCards(cards: SavedCards) {
        emptyContainer.visibility = if (cards.list.isEmpty()) View.VISIBLE else View.GONE
        mtgCardsView.loadCards(cards.list, this, R.string.action_saved, cards.filter, R.menu.card_saved_option)
    }

    override fun showCardsGrid() {
        LOG.d()
        mtgCardsView.setGridOn()
    }

    override fun showCardsList() {
        LOG.d()
        mtgCardsView.setListOn()
    }

    override fun onCardSelected(card: MTGCard?, position: Int, view: View?) {
        TrackingManager.trackOpenCard(position)
        startActivity(CardsActivity.newFavInstance(context, position))
    }

    override fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int) {
        LOG.d()
        when(menuItem.itemId){
            R.id.action_add_to_deck -> DialogHelper.open(dbActivity, "add_to_deck", AddToDeckFragment.newInstance(card))
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun openSearch(){
        LOG.d()
        startActivity(Intent(activity, SearchActivity::class.java))
    }

}
