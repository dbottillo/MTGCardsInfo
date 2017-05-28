package com.dbottillo.mtgsearchfree.ui.sets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.ui.BaseHomeFragment
import com.dbottillo.mtgsearchfree.util.AnimationUtil
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.MaterialWrapper
import com.dbottillo.mtgsearchfree.util.UIUtil
import com.dbottillo.mtgsearchfree.view.activities.CardLuckyActivity
import com.dbottillo.mtgsearchfree.view.activities.SearchActivity
import com.dbottillo.mtgsearchfree.view.adapters.OnCardListener
import com.dbottillo.mtgsearchfree.view.views.MTGCardsView
import javax.inject.Inject

class SetsFragment : BaseHomeFragment(), SetsFragmentView, OnCardListener {

    @Inject
    lateinit var preenter: SetsFragmentPresenter

    lateinit var mtgCardsView: MTGCardsView
    lateinit var tooltip: LinearLayout

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mtgApp.uiGraph.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_sets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preenter.init(this)

        view.findViewById(R.id.action_search).setOnClickListener { startActivity(Intent(activity, SearchActivity::class.java)) }
        view.findViewById(R.id.action_lucky).setOnClickListener { startActivity(Intent(activity, CardLuckyActivity::class.java)) }
        view.findViewById(R.id.main_tooltip_close).setOnClickListener {
            generalData.setTooltipMainHide()
            AnimationUtil.animateHeight(tooltip, 0)
        }

        tooltip = view.findViewById(R.id.main_tooltip) as LinearLayout
        mtgCardsView = view.findViewById(R.id.cards) as MTGCardsView
        mtgCardsView.setEmptyString(R.string.empty_cards)

        if (generalData.isTooltipMainToShow) {
            UIUtil.setHeight(tooltip, resources.getDimensionPixelSize(R.dimen.main_tooltip_height))
            MaterialWrapper.setElevation(tooltip, resources.getDimensionPixelSize(R.dimen.toolbar_elevation).toFloat())
        } else {
            UIUtil.setHeight(tooltip, 0)
        }

        setupHomeActivityScroll(recyclerView = mtgCardsView.listView)

        preenter.loadSets()
    }


    override fun getPageTrack(): String {
        return "/sets"
    }

    override fun getScrollViewId(): Int {
        return R.id.card_list
    }

    override fun getTitle(): String {
        return preenter.set()?.name?: "Aether Reveal"
    }

    override fun showSet(set: MTGSet, cards: List<MTGCard>, filter: CardFilter) {
        toolbarRevealScrollHelper.updateTitle(set.name)
        mtgCardsView.loadCards(cards, this, set.name, filter, R.menu.card_option)
    }

    override fun onCardsViewTypeSelected() {
        preenter.toggleCardTypeViewPreference()
    }

    override fun onCardsSettingSelected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCardSelected(card: MTGCard?, position: Int, view: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOptionSelected(menuItem: MenuItem?, card: MTGCard?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showCardsGrid() {
        LOG.d()
        mtgCardsView.setGridOn()
    }

    override fun showCardsList() {
        LOG.d()
        mtgCardsView.setListOn()
    }
}