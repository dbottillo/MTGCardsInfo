package com.dbottillo.mtgsearchfree.ui.decks

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import butterknife.OnClick
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.ui.BaseHomeFragment
import com.dbottillo.mtgsearchfree.ui.lifecounter.DecksAdapter
import com.dbottillo.mtgsearchfree.ui.lifecounter.OnDecksListener
import com.dbottillo.mtgsearchfree.util.DialogUtil
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.PermissionUtil
import com.dbottillo.mtgsearchfree.util.TrackingManager
import javax.inject.Inject

class DecksFragment : BaseHomeFragment(), DecksFragmentView, OnDecksListener, PermissionUtil.PermissionListener {

    private val READ_REQUEST_CODE = 42

    lateinit var decksList: RecyclerView

    @Inject
    lateinit var presenter: DecksFragmentPresenter

    @Inject
    lateinit var dialogUtil: DialogUtil

    internal lateinit var adapter: DecksAdapter
    internal var decks: MutableList<Deck> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_decks, container, false)
        mtgApp.uiGraph.inject(this)
        dialogUtil.init(context)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        decksList = view.findViewById(R.id.decks_list) as RecyclerView
        view.findViewById(R.id.action_import).setOnClickListener {
            importDeck()
        }

        decksList.setHasFixedSize(true)
        decksList.layoutManager = LinearLayoutManager(view.context)
        setupHomeActivityScroll(recyclerView = decksList)

        adapter = DecksAdapter(decks, this, delete = {
            deleteDeck(it)
        }, selected = {
            LOG.d()
            val intent = Intent(activity, DeckActivity::class.java)
            intent.putExtra("deck", it)
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

    override fun getScrollViewId(): Int {
        return R.id.decks_list
    }

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

    override fun onAddDeck() {
        dialogUtil.showAddDeck {
            presenter.addDeck(it)
            TrackingManager.trackNewDeck(it)
        }
    }

    internal fun deleteDeck(deck: Deck) {
        LOG.d()
        if (deck.numberOfCards > 0) {
            dialogUtil.deleteDeck(deck, {
                presenter.deleteDeck(it)
                TrackingManager.trackDeleteDeck(deck.name)
            })

        } else {
            presenter.deleteDeck(deck)
            TrackingManager.trackDeleteDeck(deck.name)
        }
    }

    internal fun importDeck(){
        LOG.d()
        dbActivity.requestPermission(PermissionUtil.TYPE.READ_STORAGE, this)
    }

    override fun showError(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun permissionGranted() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.action = Intent.ACTION_OPEN_DOCUMENT
        } else {
            intent.action = Intent.ACTION_GET_CONTENT
        }
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
            val uri: Uri
            if (resultData != null) {
                uri = resultData.data
                presenter.importDeck(uri)
            }
        }
    }
}