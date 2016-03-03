package com.dbottillo.mtgsearchfree.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.*
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.adapters.CardListAdapter
import com.dbottillo.mtgsearchfree.adapters.GameSetAdapter
import com.dbottillo.mtgsearchfree.adapters.OnCardListener
import com.dbottillo.mtgsearchfree.cards.CardsHelper
import com.dbottillo.mtgsearchfree.communication.DataManager
import com.dbottillo.mtgsearchfree.communication.events.CardsEvent
import com.dbottillo.mtgsearchfree.communication.events.SetEvent
import com.dbottillo.mtgsearchfree.component.AppComponent
import com.dbottillo.mtgsearchfree.database.CardDataSource
import com.dbottillo.mtgsearchfree.helper.LOG
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.resources.MTGSet
import com.dbottillo.mtgsearchfree.util.DialogUtil
import com.dbottillo.mtgsearchfree.view.activities.MainActivity
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar
import java.util.*

class MainFragment : BasicFragment(), DialogUtil.SortDialogListener,
        MainActivity.MainActivityListener, OnCardListener {

    override fun onCardSelected(card: MTGCard?, position: Int) {
        throw UnsupportedOperationException()
    }

    override fun onOptionSelected(menuItem: MenuItem?, card: MTGCard?, position: Int) {
        throw UnsupportedOperationException()
    }

    override fun getPageTrack(): String? {
        return "/set";
    }

    override fun setupComponent(appComponent: AppComponent) {
        throw UnsupportedOperationException()
    }

    private var gameSet: MTGSet? = null
    private var sets: ArrayList<MTGSet>? = null
    private var cards: ArrayList<MTGCard>? = null
    private var adapter: CardListAdapter? = null
    private var setAdapter: GameSetAdapter? = null
    private var listView: ListView? = null
    private var emptyView: TextView? = null
    private var setArrow: ImageView? = null
    private var setListBg: View? = null
    private var setList: ListView? = null
    private var currentSetPosition = -1
    private var container: View? = null
    private var progressBar: SmoothProgressBar? = null
    var chooserName: TextView? = null
    var currentFilter: CardFilter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_main, container, false)

        setActionBarTitle(getString(R.string.app_long_name))

        this.container = rootView.findViewById(R.id.container)
        setListBg = rootView.findViewById(R.id.set_list_bg)
        setList = rootView.findViewById(R.id.set_list) as ListView
        setArrow = rootView.findViewById(R.id.set_arrow) as ImageView
        chooserName = rootView.findViewById(R.id.set_chooser_name) as TextView

        emptyView = rootView.findViewById(R.id.empty_view) as TextView
        emptyView?.setText(R.string.empty_cards)
        listView = rootView.findViewById(R.id.card_list) as ListView

        cards = ArrayList<MTGCard>()
        adapter = CardListAdapter(activity, cards, false, R.menu.card_option, this)
        listView!!.adapter = adapter

        progressBar = rootView.findViewById(R.id.progress) as SmoothProgressBar

        setListBg!!.setOnClickListener({
            if (setList!!.height > 0) {
                showHideSetList(false)
            }
        })

        rootView.findViewById(R.id.cards_sort).setOnClickListener({
            DialogUtil.chooseSortDialog(context, sharedPreferences, this@MainFragment)
        })


        if (savedInstanceState == null) {
            sets = ArrayList<MTGSet>()
            DataManager.execute(DataManager.TASK.SET_LIST)
        } else {
            sets = savedInstanceState.getParcelableArrayList<MTGSet>("SET")
            currentSetPosition = savedInstanceState.getInt("currentSetPosition")
            if (currentSetPosition < 0) {
                DataManager.execute(DataManager.TASK.SET_LIST)
            } else {
                loadSet()
            }
        }

        setAdapter = GameSetAdapter(activity.applicationContext, sets)
        setAdapter!!.setCurrent(currentSetPosition)
        setList!!.adapter = setAdapter
        setList!!.onItemClickListener = AdapterView.OnItemClickListener {
            parent, view, position, id ->
            if (currentSetPosition != position) {
                currentSetPosition = position
                showHideSetList(true)
            } else {
                showHideSetList(false)
            }
        }

        rootView.findViewById(R.id.set_chooser).setOnClickListener({ showHideSetList(false) })

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context as MainActivity).setMainActivityListener(this)
        currentFilter = context.currentFilter
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putInt("currentSetPosition", currentSetPosition)
        outState.putParcelableArrayList("SET", sets)
    }

    private fun showHideSetList(loadSet: Boolean) {
        val startHeight = setList!!.height
        val targetHeight = if ((startHeight == 0)) container!!.height else 0
        val startRotation = setArrow!!.rotation
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                super.applyTransformation(interpolatedTime, t)
                if (targetHeight > startHeight) {
                    val newHeight = (startHeight + (interpolatedTime * targetHeight)).toInt()
                    setHeightView(setList!!, newHeight)
                    setHeightView(setListBg!!, newHeight)
                    setArrow!!.rotation = startRotation + (180 * interpolatedTime)
                } else {
                    val newHeight = (startHeight - startHeight * interpolatedTime).toInt()
                    setHeightView(setList!!, newHeight)
                    setHeightView(setListBg!!, newHeight)
                    setArrow!!.rotation = startRotation - (180 * interpolatedTime)
                }
            }
        }
        animation.duration = 200
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                if (loadSet) {
                    /*if (!getApp().isPremium() && position > 2){
                                            showGoToPremium();
                                            return false;
                                        }*/
                    TrackingHelper.getInstance(activity.applicationContext).trackEvent(TrackingHelper.UA_CATEGORY_SET, TrackingHelper.UA_ACTION_SELECT, sets!![currentSetPosition].code)
                    val editor = sharedPreferences.edit()
                    editor.putInt("setPosition", currentSetPosition)
                    editor.apply()
                    setAdapter!!.setCurrent(currentSetPosition)
                    setAdapter!!.notifyDataSetChanged()
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

    fun onEventMainThread(event: SetEvent) {
        if (event.isError) {
            Toast.makeText(activity, event.errorMessage, Toast.LENGTH_SHORT).show()
            TrackingHelper.getInstance(activity.applicationContext).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "set-main", event.getErrorMessage())
        } else {
            currentSetPosition = sharedPreferences.getInt("setPosition", 0)
            setAdapter!!.setCurrent(currentSetPosition)
            sets!!.clear()
            for (set in event.getResult()) {
                sets!!.add(set)
            }
            setAdapter!!.notifyDataSetChanged()
            loadSet()
        }
        bus.removeStickyEvent(event)
    }

    private fun loadSet() {
        gameSet = sets!!.get(currentSetPosition)
        chooserName?.text = gameSet?.name
        DataManager.execute(DataManager.TASK.SET_CARDS, gameSet)
    }

    fun onEventMainThread(event: CardsEvent) {
        LOG.e("on event main thread cards")
        if (event.isError) {
            Toast.makeText(activity, event.errorMessage, Toast.LENGTH_SHORT).show()
            TrackingHelper.getInstance(activity).trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "card-main", event.errorMessage)
        } else {
            gameSet!!.clear()
            for (card in event.result) {
                gameSet!!.addCard(card)
            }
            updateContent()
        }
        bus.removeStickyEvent(event)
    }

    fun updateContent() {
        LOG.e("update content")
        cards!!.clear()
        CardsHelper.filterCards(currentFilter, gameSet!!.cards, cards)
        val wubrgSort = sharedPreferences.getBoolean(BasicFragment.PREF_SORT_WUBRG, true)
        CardsHelper.sortCards(wubrgSort, cards)

        adapter!!.notifyDataSetChanged()
        emptyView!!.visibility = if (adapter!!.count == 0) View.VISIBLE else View.GONE
        listView!!.smoothScrollToPosition(0)

        if (gameSet!!.cards.size == CardDataSource.LIMIT) {
            val footer = LayoutInflater.from(activity).inflate(R.layout.search_bottom, listView, false)
            val moreResult = footer.findViewById(R.id.more_result) as TextView
            moreResult.text = resources.getQuantityString(R.plurals.search_limit, CardDataSource.LIMIT, CardDataSource.LIMIT)
            listView!!.addFooterView(footer)
        }
        progressBar!!.visibility = View.GONE

        emptyView!!.visibility = if (adapter!!.count == 0) View.VISIBLE else View.GONE
    }

    override fun updateContent(filter: CardFilter) {
        currentFilter = filter
        updateContent()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSortSelected() {
        loadSet()
    }
}
