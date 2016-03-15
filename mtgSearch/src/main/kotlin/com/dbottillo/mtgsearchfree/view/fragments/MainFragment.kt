package com.dbottillo.mtgsearchfree.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.*
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.bindView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.adapters.CardListAdapter
import com.dbottillo.mtgsearchfree.adapters.GameSetAdapter
import com.dbottillo.mtgsearchfree.adapters.OnCardListener
import com.dbottillo.mtgsearchfree.base.MTGApp
import com.dbottillo.mtgsearchfree.cards.CardsHelper
import com.dbottillo.mtgsearchfree.database.CardDataSource
import com.dbottillo.mtgsearchfree.dialog.AddToDeckFragment
import com.dbottillo.mtgsearchfree.helper.DialogHelper
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.tracking.TrackingManager
import com.dbottillo.mtgsearchfree.util.DialogUtil
import com.dbottillo.mtgsearchfree.view.activities.CardsActivity
import com.dbottillo.mtgsearchfree.view.activities.MainActivity
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar
import java.util.*
import javax.inject.Inject

class MainFragment : BasicFragment(), DialogUtil.SortDialogListener,
        MainActivity.MainActivityListener, OnCardListener, CardsView, SetsView {

    @Inject lateinit var cardsPresenter: CardsPresenter
    @Inject lateinit var setsPresenter: SetsPresenter

    private var gameSet: MTGSet? = null
    private var sets: ArrayList<MTGSet> = ArrayList()
    private var cardBucket: CardsBucket? = null
    private var cards: ArrayList<MTGCard> = ArrayList()
    private var adapter: CardListAdapter? = null
    private var setAdapter: GameSetAdapter? = null
    private var currentSetPosition = -1
    var mainActivity: MainActivity? = null

    val listView: ListView by bindView(R.id.card_list)
    val emptyView: TextView by bindView(R.id.empty_view)
    val setArrow: ImageView by bindView(R.id.set_arrow)
    val setListBg: View by bindView(R.id.set_list_bg)
    val setList: ListView by bindView(R.id.set_list)
    val container: View by bindView(R.id.container)
    val progressBar: SmoothProgressBar by bindView(R.id.progress)
    val chooserName: TextView by bindView(R.id.set_chooser_name)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_main, container, false)
        ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBarTitle(getString(R.string.app_long_name))

        adapter = CardListAdapter(activity, cards, false, R.menu.card_option, this)
        listView.adapter = adapter

        MTGApp.dataGraph.inject(this)
        cardsPresenter.init(this)
        setsPresenter.init(this)
        setsPresenter.loadSets();

        setAdapter = GameSetAdapter(activity.applicationContext, sets)
        setAdapter!!.setCurrent(currentSetPosition)
        setList.adapter = setAdapter
        setList.onItemClickListener = AdapterView.OnItemClickListener {
            parent, view, position, id ->
            if (currentSetPosition != position) {
                currentSetPosition = position
                showHideSetList(true)
            } else {
                showHideSetList(false)
            }
        }

        view?.findViewById(R.id.set_chooser)?.setOnClickListener({ showHideSetList(false) })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        mainActivity?.setMainActivityListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentSetPosition", currentSetPosition)
        outState.putParcelableArrayList("SET", sets)
    }

    @OnClick(R.id.cards_sort)
    fun onSortClicked(view: View) {
        DialogUtil.chooseSortDialog(context, sharedPreferences, this)
    }

    @OnClick(R.id.set_list_bg)
    fun onSetListBgClicked(view: View) {
        if (setList.height > 0) {
            showHideSetList(false)
        }
    }

    private fun showHideSetList(loadSet: Boolean) {
        val startHeight = setList.height
        val targetHeight = if ((startHeight == 0)) container.height else 0
        val startRotation = setArrow.rotation
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                super.applyTransformation(interpolatedTime, t)
                if (targetHeight > startHeight) {
                    val newHeight = (startHeight + (interpolatedTime * targetHeight)).toInt()
                    setHeightView(setList, newHeight)
                    setHeightView(setListBg, newHeight)
                    setArrow.rotation = startRotation + (180 * interpolatedTime)
                } else {
                    val newHeight = (startHeight - startHeight * interpolatedTime).toInt()
                    setHeightView(setList, newHeight)
                    setHeightView(setListBg, newHeight)
                    setArrow.rotation = startRotation - (180 * interpolatedTime)
                }
            }
        }
        animation.duration = 200
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                if (loadSet) {
                    TrackingManager.trackSet(gameSet, sets[currentSetPosition])
                    val editor = sharedPreferences.edit()
                    editor.putInt("setPosition", currentSetPosition)
                    editor.apply()
                    setAdapter?.setCurrent(currentSetPosition)
                    setAdapter?.notifyDataSetChanged()
                    loadSet()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        view!!.startAnimation(animation)
    }

    private fun setHeightView(view: View, value: Int) {
        val params = view.layoutParams as RelativeLayout.LayoutParams
        params.height = value
        view.layoutParams = params
    }

    override fun favIdLoaded(favourites: IntArray) {
        // favourites are not needed in this fragment
        throw UnsupportedOperationException()
    }

    override fun setsLoaded(sets: List<MTGSet>) {
        currentSetPosition = sharedPreferences.getInt("setPosition", 0)
        setAdapter?.setCurrent(currentSetPosition)
        this.sets.clear()
        for (set in sets) {
            this.sets.add(set)
        }
        setAdapter?.notifyDataSetChanged()
        loadSet()
    }

    override fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun loadSet() {
        gameSet = sets[currentSetPosition]
        chooserName.text = gameSet?.name
        cardsPresenter.loadCards(gameSet!!)
    }

    override fun luckyCardsLoaded(cards: ArrayList<MTGCard>) {
        throw UnsupportedOperationException()
    }

    override fun cardLoaded(bucket: CardsBucket) {
        cardBucket = bucket
        updateContent()
    }

    override fun updateContent() {
        cards.clear()
        CardsHelper.filterCards(mainActivity?.currentFilter!!, cardBucket?.cards!!, cards)
        val wubrgSort = sharedPreferences.getBoolean(BasicFragment.PREF_SORT_WUBRG, true)
        CardsHelper.sortCards(wubrgSort, cards)

        adapter?.notifyDataSetChanged()
        emptyView.visibility = if (adapter?.count == 0) View.VISIBLE else View.GONE
        listView.smoothScrollToPosition(0)

        if (cards.size == CardDataSource.LIMIT) {
            val footer = LayoutInflater.from(activity).inflate(R.layout.search_bottom, listView, false)
            val moreResult = footer.findViewById(R.id.more_result) as TextView
            moreResult.text = resources.getQuantityString(R.plurals.search_limit, CardDataSource.LIMIT, CardDataSource.LIMIT)
            listView.addFooterView(footer)
        }
        progressBar.visibility = View.GONE

        emptyView.visibility = if (adapter?.count == 0) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSortSelected() {
        loadSet()
    }

    override fun onCardSelected(card: MTGCard?, position: Int) {
        TrackingManager.trackCard(gameSet, position)
        val cardsView = Intent(activity, CardsActivity::class.java)
        cardsView.putExtra(CardsActivity.POSITION, position)
        cardsView.putExtra(CardsActivity.KEY_SET, gameSet)
        startActivity(cardsView)
    }

    override fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int) {
        if (menuItem.itemId == R.id.action_add_to_deck) {
            DialogHelper.open(dbActivity!!, "add_to_deck", AddToDeckFragment.newInstance(card))
        } else {
            cardsPresenter.saveAsFavourite(card)
        }
    }

    override fun getPageTrack(): String? {
        return "/set";
    }

}
