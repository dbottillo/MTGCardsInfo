package com.dbottillo.mtgsearchfree.decks

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dbottillo.mtgsearchfree.decks.deck.DeckActivity
import com.dbottillo.mtgsearchfree.home.BaseHomeFragment
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.util.DialogUtil
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.PermissionAvailable
import com.dbottillo.mtgsearchfree.util.PermissionUtil
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class DecksFragment : BaseHomeFragment(), DecksFragmentView, PermissionUtil.PermissionListener {

    private lateinit var decksList: RecyclerView

    @Inject lateinit var presenter: DecksFragmentPresenter
    @Inject lateinit var dialogUtil: DialogUtil

    internal lateinit var adapter: DecksAdapter
    internal var decks: MutableList<Deck> = mutableListOf()

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_decks, container, false)
        dialogUtil.init(context)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        decksList = view.findViewById(R.id.decks_list)
        view.findViewById<View>(R.id.action_import).setOnClickListener {
            importDeck()
        }
        view.findViewById<View>(R.id.add_new_deck).setOnClickListener {
            onAddDeck()
        }

        decksList.setHasFixedSize(true)
        decksList.layoutManager = LinearLayoutManager(view.context)

        adapter = DecksAdapter(decks,
                copy = {
                    presenter.copyDeck(it)
                },
                delete = {
                    deleteDeck(it)
                },
                selected = {
                    LOG.d()
                    val intent = Intent(activity, DeckActivity::class.java)
                    intent.putExtra("deck", it.id)
                    startActivity(intent)
                })
        decksList.adapter = adapter

        presenter.init(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.loadDecks()
    }

    override fun getTitle(): String {
        return getString(R.string.action_decks)
    }

    override fun getScrollViewId() = R.id.decks_list
    override fun getToolbarId() = R.id.toolbar
    override fun getToolbarTitleId() = R.id.toolbar_title

    override fun getPageTrack(): String {
        return "/decks"
    }

    override fun decksLoaded(decks: List<Deck>) {
        this.decks.clear()
        decks.forEach {
            this.decks.add(it)
        }
        adapter.notifyDataSetChanged()
    }

    private fun onAddDeck() {
        dialogUtil.showAddDeck(R.layout.dialog_add_new_deck, R.id.deck_name) {
            presenter.addDeck(it)
            trackingManager.trackNewDeck(it)
        }
    }

    private fun deleteDeck(deck: Deck) {
        LOG.d()
        if (deck.numberOfCards > 0) {
            dialogUtil.deleteDeck(deck) {
                presenter.deleteDeck(it)
                trackingManager.trackDeleteDeck(deck.name)
            }
        } else {
            presenter.deleteDeck(deck)
            trackingManager.trackDeleteDeck(deck.name)
        }
    }

    private fun importDeck() {
        LOG.d()
        dbActivity.requestPermission(PermissionAvailable.ReadStorage, this)
    }

    override fun showError(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun permissionGranted() {
        val intent = Intent()
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.type = "*/*"
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun permissionNotGranted() {
        Toast.makeText(context, R.string.error_export_db, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            resultData?.data?.let {
                presenter.importDeck(it)
            }
        }
    }
}

private const val READ_REQUEST_CODE = 42