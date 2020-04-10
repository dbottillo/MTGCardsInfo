package com.dbottillo.mtgsearchfree.lifecounter

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.Player
import com.dbottillo.mtgsearchfree.storage.CardsPreferences
import com.dbottillo.mtgsearchfree.home.BaseHomeFragment
import com.dbottillo.mtgsearchfree.util.DialogUtil
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import dagger.android.support.AndroidSupportInjection
import java.util.Random
import javax.inject.Inject

class LifeCounterFragment : BaseHomeFragment(), LifeCounterView, OnLifeCounterListener {

    private lateinit var lifeCounterList: RecyclerView

    @Inject lateinit var lifeCounterPresenter: LifeCounterPresenter
    @Inject lateinit var cardsPreferences: CardsPreferences
    @Inject lateinit var dialogUtil: DialogUtil

    private lateinit var adapter: LifeCounterAdapter
    private var players: MutableList<Player> = mutableListOf()
    private var diceShowed: Boolean = false

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_life_counter, container, false)
        dialogUtil.init(context)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifeCounterList = view.findViewById(R.id.life_counter_list)
        view.findViewById<View>(R.id.action_reset).setOnClickListener { reset() }
        view.findViewById<View>(R.id.action_dice).setOnClickListener { launchDice() }
        view.findViewById<View>(R.id.add_new_player).setOnClickListener { addPlayer() }

        lifeCounterList.setHasFixedSize(true)
        lifeCounterList.layoutManager = LinearLayoutManager(view.context)

        setupMenu()

        adapter = LifeCounterAdapter(players, this, cardsPreferences.showPoison())
        lifeCounterList.adapter = adapter

        lifeCounterPresenter.init(this)
        lifeCounterPresenter.loadPlayers()
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

    override fun getScrollViewId() = R.id.life_counter_list
    override fun getToolbarId() = R.id.toolbar
    override fun getToolbarTitleId() = R.id.toolbar_title

    override fun getTitle(): String {
        return resources.getString(R.string.action_life_counter)
    }

    override fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun playersLoaded(newPlayers: List<Player>) {
        players.clear()
        players.addAll(newPlayers)

        adapter.notifyDataSetChanged()
    }

    override fun showError(exception: MTGException) {
        Toast.makeText(activity, exception.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onLifeCountChange(player: Player, value: Int) {
        TrackingManager.trackLifeCountChanged()
        player.changeLife(value)
        lifeCounterPresenter.editPlayer(player)
    }

    override fun onPoisonCountChange(player: Player, value: Int) {
        LOG.d()
        TrackingManager.trackPoisonCountChanged()
        player.changePoisonCount(value)
        lifeCounterPresenter.editPlayer(player)
    }

    override fun onEditPlayer(player: Player) {
        LOG.d()
        dialogUtil.showEditPlayer(R.layout.dialog_edit_deck, R.id.edit_text, player) {
            player.name = it
            lifeCounterPresenter.editPlayer(player)
            TrackingManager.trackEditPlayer()
        }
    }

    override fun onRemovePlayer(player: Player) {
        LOG.d()
        lifeCounterPresenter.removePlayer(player)
        TrackingManager.trackRemovePlayer()
    }

    private fun addPlayer() {
        LOG.d()
        if (lifeCounterList.adapter?.itemCount == 10) {
            Toast.makeText(activity, R.string.maximum_player, Toast.LENGTH_SHORT).show()
            return
        }
        lifeCounterPresenter.addPlayer()
        TrackingManager.trackAddPlayer()
    }

    private fun setupMenu() {
        toolbar.inflateMenu(R.menu.life_counter)

        refreshMenu()
    }

    private fun refreshMenu() {
        val poison = toolbar.menu.findItem(R.id.action_poison)
        poison.isChecked = cardsPreferences.showPoison()
        val screenOn = toolbar.menu.findItem(R.id.action_screen_on)
        screenOn.isChecked = cardsPreferences.screenOn()
        val twoHg = toolbar.menu.findItem(R.id.action_two_hg)
        twoHg.isChecked = cardsPreferences.twoHGEnabled()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
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

    private fun poisonChanged() {
        TrackingManager.trackChangePoisonSetting()
        val showPoison = !cardsPreferences.showPoison()
        cardsPreferences.showPoison(showPoison)
        refreshMenu()
        adapter.setShowPoison(showPoison)
        adapter.notifyDataSetChanged()
    }

    private fun screenOnChanged() {
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
        lifeCounterPresenter.editPlayers(players)
    }

    fun reset() {
        resetLifeCounter()
        TrackingManager.trackResetLifeCounter()
    }

    private fun launchDice() {
        if (diceShowed) {
            players.forEach { it.diceResult = -1 }
        } else {
            players.forEach { it.diceResult = Random().nextInt(20) + 1 }
        }
        diceShowed = !diceShowed
        adapter.notifyDataSetChanged()
        TrackingManager.trackLunchDice()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifeCounterPresenter.onDestroy()
    }
}
