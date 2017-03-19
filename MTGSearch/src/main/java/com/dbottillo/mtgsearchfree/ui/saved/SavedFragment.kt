package com.dbottillo.mtgsearchfree.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.BindView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.CardsBucket
import com.dbottillo.mtgsearchfree.model.DeckBucket
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter
import com.dbottillo.mtgsearchfree.ui.BaseHomeFragment
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.view.CardFilterView
import com.dbottillo.mtgsearchfree.view.CardsView
import com.dbottillo.mtgsearchfree.view.activities.CardsActivity
import com.dbottillo.mtgsearchfree.view.adapters.OnCardListener
import com.dbottillo.mtgsearchfree.view.fragments.AddToDeckFragment
import com.dbottillo.mtgsearchfree.view.helpers.CardsHelper
import com.dbottillo.mtgsearchfree.view.helpers.DialogHelper
import com.dbottillo.mtgsearchfree.view.views.MTGCardsView
import javax.inject.Inject

class SavedFragment : BaseHomeFragment(), CardsView, OnCardListener, CardFilterView {

    @BindView(R.id.cards)
    internal lateinit var mtgCardsView: MTGCardsView

    @Inject
    lateinit var cardsPresenter: CardsPresenter

    @Inject
    lateinit var cardsHelper: CardsHelper

    @Inject
    lateinit var filterPresenter: CardFilterPresenter

    lateinit var filter: CardFilter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_saved, container, false)
        mtgApp.uiGraph.inject(this)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardsPresenter.init(this)
        filterPresenter.init(this)
    }

    override fun onResume() {
        super.onResume()

        filterPresenter.loadFilter()
    }

    override fun filterLoaded(filter: CardFilter) {
        this.filter = filter
        cardsPresenter.loadFavourites()
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

    override fun cardsLoaded(bucket: CardsBucket?) {
        val filteredBucket = cardsHelper.filterCards(filter, bucket)
        cardsHelper.sortCards(bucket)
        mtgCardsView.loadCards(filteredBucket, this, R.string.action_saved, filter)
    }

    override fun deckLoaded(bucket: DeckBucket?) {
    }

    override fun favIdLoaded(favourites: IntArray?) {
    }

    override fun cardTypePreferenceChanged(grid: Boolean) {
        LOG.d()
        if (grid) {
            mtgCardsView.setGridOn()
        } else {
            mtgCardsView.setListOn()
        }
    }

    override fun onCardSelected(card: MTGCard?, position: Int, view: View?) {
        TrackingManager.trackOpenCard(position)
        startActivity(CardsActivity.newFavInstance(context, position))
    }

    override fun onOptionSelected(menuItem: MenuItem?, card: MTGCard?, position: Int) {
        LOG.d()
        when(menuItem?.itemId){
            R.id.action_add_to_deck -> DialogHelper.open(dbActivity, "add_to_deck", AddToDeckFragment.newInstance(card))
            R.id.action_remove -> {
                cardsPresenter.removeFromFavourite(card, false)
                cardsPresenter.loadFavourites()
            }
        }
    }

    override fun onCardsViewTypeSelected() {
        LOG.d()
        cardsPresenter.toggleCardTypeViewPreference()
    }

    override fun onCardsSettingSelected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
