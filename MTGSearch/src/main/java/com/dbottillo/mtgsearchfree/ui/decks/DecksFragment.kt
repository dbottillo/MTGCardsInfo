package com.dbottillo.mtgsearchfree.ui.decks

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.BindView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.exceptions.MTGException
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckBucket
import com.dbottillo.mtgsearchfree.presenter.DecksPresenter
import com.dbottillo.mtgsearchfree.ui.BaseHomeFragment
import com.dbottillo.mtgsearchfree.ui.lifecounter.DecksAdapter
import com.dbottillo.mtgsearchfree.ui.lifecounter.OnDecksListener
import com.dbottillo.mtgsearchfree.view.DecksView
import javax.inject.Inject

class DecksFragment : BaseHomeFragment(), DecksView, OnDecksListener {

    @BindView(R.id.decks_list)
    lateinit var decksList: RecyclerView

    @Inject
    lateinit var decksPresenter: DecksPresenter

    internal lateinit var adapter: DecksAdapter
    internal var decks: MutableList<Deck> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_decks, container, false)
        mtgApp.uiGraph.inject(this)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        decksList.setHasFixedSize(true)
        decksList.layoutManager = LinearLayoutManager(view?.context)
        setupHomeActivityScroll(recyclerView = decksList)

        adapter = DecksAdapter(decks, this)
        decksList.adapter = adapter

        decksPresenter.init(this)
    }

    override fun onResume() {
        super.onResume()
        decksPresenter.loadDecks()
    }

    override fun getScrollViewId(): Int {
        return R.id.decks_list
    }

    override fun getPageTrack(): String {
        return "/decks"
    }

    override fun showError(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun showError(exception: MTGException?) {
        Toast.makeText(activity, exception?.getLocalizedMessage(context), Toast.LENGTH_SHORT).show()
    }

    override fun decksLoaded(newDecks: MutableList<Deck>) {
        decks.clear()
        newDecks.forEach {
            decks.add(it)
        }
    }

    override fun deckLoaded(bucket: DeckBucket?) {
        throw UnsupportedOperationException()
    }

    override fun deckExported(success: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun onAddDeck() {
        //openNewDeck()
    }
/*
    override fun onBackPressed(): Boolean {
        if (newDeckViewOpen) {
            closeNewDeck()
            return true
        }
        return super.onBackPressed()
    }

    private fun openNewDeck() {
        LOG.d()
        newDeckOverlay.alpha = 0.0f
        newDeckOverlay.visibility = View.VISIBLE
        newDeckOverlay.animate().alpha(1.0f).setDuration(250).setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                newDeckName.requestFocus()
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        }).start()
        newDeckViewOpen = true

    }

    private fun closeNewDeck() {
        LOG.d()
        newDeckOverlay.requestFocus()
        newDeckOverlay.animate().alpha(0.0f).setDuration(250).setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                newDeckOverlay.visibility = View.GONE
                newDeckName.setText("")
                InputUtil.hideKeyboard(activity, newDeckName.windowToken)
                newDeckViewOpen = false
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        }).start()
    }*/

}
