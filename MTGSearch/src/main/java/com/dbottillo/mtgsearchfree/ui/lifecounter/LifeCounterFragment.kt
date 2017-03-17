package com.dbottillo.mtgsearchfree.ui.lifecounter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import butterknife.BindView
import butterknife.OnClick
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.model.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.presenter.PlayerPresenter
import com.dbottillo.mtgsearchfree.ui.BaseHomeFragment
import com.dbottillo.mtgsearchfree.util.DialogUtil
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.view.PlayersView
import com.dbottillo.mtgsearchfree.view.views.MTGLoader
import java.util.*
import javax.inject.Inject

class LifeCounterFragment : BaseHomeFragment(), PlayersView, OnLifeCounterListener {

    @BindView(R.id.loader)
    lateinit var loader: MTGLoader

    @BindView(R.id.life_counter_list)
    lateinit var lifeCounterList: RecyclerView

    @Inject
    internal lateinit var playerPresenter: PlayerPresenter

    @Inject
    internal lateinit var cardsPreferences: CardsPreferences

    @Inject
    lateinit var dialogUtil : DialogUtil

    internal lateinit var adapter: LifeCounterAdapter
    internal var players: MutableList<Player> = mutableListOf()

    internal var diceShowed : Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_life_counter, container, false)
        mtgApp.uiGraph.inject(this)
        dialogUtil.init(context)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifeCounterList.setHasFixedSize(true)
        lifeCounterList.layoutManager = LinearLayoutManager(view?.context)
        setupHomeActivityScroll(recyclerView = lifeCounterList)

        setupMenu()

        adapter = LifeCounterAdapter(players, this, cardsPreferences.showPoison())
        lifeCounterList.adapter = adapter

        playerPresenter.init(this)
        playerPresenter.loadPlayers()

    }

    override fun onResume() {
        super.onResume()
        setScreenOn(cardsPreferences.screenOn())
    }

    override fun onPause() {
        super.onPause()
        setScreenOn(false)
    }

    override fun getPageTrack(): String {
        return "/life_counter"
    }

    override fun getScrollViewId(): Int {
        return R.id.life_counter_list
    }

    override fun getTitle(): String {
        return context.getString(R.string.action_life_counter)
    }

    override fun showError(message: String?) {
        loader.visibility = View.VISIBLE
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun playersLoaded(newPlayers: List<Player>) {
        loader.visibility = View.GONE

        players.clear()
        players.addAll(newPlayers)

        adapter.notifyDataSetChanged()
    }

    override fun showError(exception: MTGException?) {
        loader.visibility = View.VISIBLE
        Toast.makeText(activity, exception?.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        loader.visibility = View.VISIBLE
    }

    override fun onLifeCountChange(player: Player, value: Int) {
        TrackingManager.trackLifeCountChanged()
        player.changeLife(value)
        playerPresenter.editPlayer(player)
    }

    override fun onPoisonCountChange(player: Player, value: Int) {
        LOG.d()
        TrackingManager.trackPoisonCountChanged()
        player.changePoisonCount(value)
        playerPresenter.editPlayer(player)
    }

    override fun onEditPlayer(player: Player) {
        LOG.d()
        dialogUtil.showEditPlayer(player){
            player.name = it
            playerPresenter.editPlayer(player)
            TrackingManager.trackEditPlayer()
        }
    }

    override fun onRemovePlayer(player: Player) {
        LOG.d()
        playerPresenter.removePlayer(player)
        TrackingManager.trackRemovePlayer()
    }

    override fun onAddPlayer() {
        LOG.d()
        if (lifeCounterList.adapter.itemCount == 10) {
            Toast.makeText(activity, R.string.maximum_player, Toast.LENGTH_SHORT).show()
            return
        }
        playerPresenter.addPlayer()
        TrackingManager.trackAddPlayer()
    }

    internal fun setupMenu(){
        toolbar.inflateMenu(R.menu.life_counter)

        refreshMenu()
    }

    internal fun refreshMenu(){
        val poison = toolbar.menu.findItem(R.id.action_poison)
        poison.isChecked = cardsPreferences.showPoison()
        val screenOn = toolbar.menu.findItem(R.id.action_screen_on)
        screenOn.isChecked = cardsPreferences.screenOn()
        val twoHg = toolbar.menu.findItem(R.id.action_two_hg)
        twoHg.isChecked = cardsPreferences.twoHGEnabled()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_poison -> poisonChanged()
            R.id.action_screen_on -> screenOnChanged()
            R.id.action_two_hg -> twoHGChanged()
            else -> super.onMenuItemClick(item)
        }
        return true
    }

    private fun twoHGChanged() {
        val twoHGEnabled = !cardsPreferences.twoHGEnabled()
        cardsPreferences.setTwoHGEnabled(twoHGEnabled)
        refreshMenu()
        resetLifeCounter()
        TrackingManager.trackHGLifeCounter()
    }

    fun poisonChanged(){
        TrackingManager.trackChangePoisonSetting()
        val showPoison = !cardsPreferences.showPoison()
        cardsPreferences.showPoison(showPoison)
        refreshMenu()
        adapter.setShowPoison(showPoison)
        adapter.notifyDataSetChanged()
    }

    fun screenOnChanged(){
        val screenOn = cardsPreferences.screenOn()
        cardsPreferences.setScreenOn(!screenOn)
        refreshMenu()
        setScreenOn(!screenOn)
        TrackingManager.trackScreenOn()
    }

    private fun setScreenOn(screenOn: Boolean) {
        LOG.d()
        if (view != null) {
            view!!.keepScreenOn = screenOn
        }
    }

    private fun resetLifeCounter() {
        LOG.d()
        val twoHGEnabled = cardsPreferences.twoHGEnabled()
        for (player in players) {
            player.life = if (twoHGEnabled) 30 else 20
            player.poisonCount = if (twoHGEnabled) 15 else 10
        }
        playerPresenter.editPlayers(players)
    }

    @OnClick(R.id.action_reset)
    fun reset(){
        resetLifeCounter()
        TrackingManager.trackResetLifeCounter()
    }

    @OnClick(R.id.action_dice)
    fun launchDice(){
        if (diceShowed) {
            players.forEach { it.diceResult = -1 }

        } else {
            players.forEach { it.diceResult = Random().nextInt(20) + 1 }
        }
        diceShowed = !diceShowed
        adapter.notifyDataSetChanged()
        TrackingManager.trackLunchDice()
    }
}
