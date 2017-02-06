package com.dbottillo.mtgsearchfree.ui.lifecounter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
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
import com.dbottillo.mtgsearchfree.ui.HomeActivity
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.view.PlayersView
import com.dbottillo.mtgsearchfree.view.views.MTGLoader
import javax.inject.Inject


class NewLifeCounterFragment : BaseHomeFragment(), PlayersView, OnLifeCounterListener {

    @BindView(R.id.loader)
    lateinit var loader: MTGLoader

    @BindView(R.id.life_counter_list)
    lateinit var lifeCounterList: RecyclerView

    @Inject
    internal lateinit var playerPresenter: PlayerPresenter

    @Inject
    internal lateinit var cardsPreferences: CardsPreferences

    internal lateinit var adapter: NewLifeCounterAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_life_counter, container, false)
        mtgApp.uiGraph.inject(this)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifeCounterList.setHasFixedSize(true)
        lifeCounterList.layoutManager = LinearLayoutManager(view?.context)
        if (activity is HomeActivity) {
            lifeCounterList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (dy > 0) {
                        (activity as HomeActivity).scrollingUp()
                    } else {
                        (activity as HomeActivity).scrollingDown()
                    }
                }

            })
        }

        playerPresenter.init(this)
        playerPresenter.loadPlayers()

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
        lifeCounterList.adapter = NewLifeCounterAdapter(newPlayers, this, cardsPreferences.showPoison())
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
        val alert = AlertDialog.Builder(activity, R.style.MTGDialogTheme)

        alert.setTitle(getString(R.string.edit_player))

        val layoutInflater = LayoutInflater.from(context)
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(R.layout.dialog_edit_deck, null)
        val editText = view.findViewById(R.id.edit_text) as EditText
        editText.setText(player.name)
        editText.setSelection(player.name.length)
        alert.setView(view)

        alert.setPositiveButton(getString(R.string.save)) { _, _ ->
            val value = editText.text.toString()
            player.name = value
            playerPresenter.editPlayer(player)
            TrackingManager.trackEditPlayer()
        }

        alert.show()
    }

    override fun onRemovePlayer(player: Player) {
        LOG.d()
        playerPresenter.removePlayer(player)
        TrackingManager.trackRemovePlayer()
    }

    @OnClick(R.id.new_player)
    fun addNewPlayer() {
        LOG.d()
        if (lifeCounterList.adapter.itemCount == 10) {
            Toast.makeText(activity, R.string.maximum_player, Toast.LENGTH_SHORT).show()
            return
        }
        playerPresenter.addPlayer()
        TrackingManager.trackAddPlayer()
    }
}
