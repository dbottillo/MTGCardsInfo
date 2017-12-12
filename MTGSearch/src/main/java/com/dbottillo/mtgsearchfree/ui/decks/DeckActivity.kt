package com.dbottillo.mtgsearchfree.ui.decks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.CardsCollection
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivity
import com.dbottillo.mtgsearchfree.ui.views.MTGLoader
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.PermissionUtil
import com.dbottillo.mtgsearchfree.util.TrackingManager
import java.util.*
import javax.inject.Inject

class DeckActivity : BasicActivity(), DeckActivityView {

    @Inject
    lateinit var presenter: DeckActivityPresenter

    private val container: View by lazy { findViewById<ViewGroup>(R.id.container) }
    private val emptyView: TextView by lazy { findViewById<TextView>(R.id.empty_view) }
    private val loader: MTGLoader by lazy { findViewById<MTGLoader>(R.id.loader) }
    private val cardList: RecyclerView by lazy { findViewById<RecyclerView>(R.id.card_list) }

    lateinit var deck: Deck

    private val deckAdapter = DeckAdapter()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_deck)

        setupToolbar()

        deck = intent.getParcelableExtra("deck")
        title = if (deck.name == null) getString(R.string.deck_title) else deck.name

        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        emptyView.setText(R.string.empty_deck)

        cardList.setHasFixedSize(true)
        cardList.layoutManager = LinearLayoutManager(this)

        deckAdapter.cardListener = object : OnDeckCardListener{
            override fun onCardSelected(card: MTGCard) {
                startActivity(CardsActivity.newInstance(this@DeckActivity, deck, deckAdapter.getCards().indexOf(card)))
            }

            override fun onOptionSelected(menuItem: MenuItem, card: MTGCard) {
                if (menuItem.itemId == R.id.action_add_one_more) {
                    TrackingManager.trackAddCardToDeck()
                    presenter.addCardToDeck(deck, card, 1)

                } else if (menuItem.itemId == R.id.action_remove_one) {
                    TrackingManager.trackRemoveCardFromDeck()
                    presenter.removeCardFromDeck(deck, card)

                } else if (menuItem.itemId == R.id.action_remove_all) {
                    TrackingManager.trackRemoveAllCardsFromDeck()
                    presenter.removeAllCardFromDeck(deck, card)

                } else if (menuItem.itemId == R.id.action_move_one) {
                    TrackingManager.trackMoveOneCardFromDeck()
                    if (card.isSideboard) {
                        presenter.moveCardFromSideBoard(deck, card, 1)
                    } else {
                        presenter.moveCardToSideBoard(deck, card, 1)
                    }

                } else if (menuItem.itemId == R.id.action_move_all) {
                    TrackingManager.trackMoveAllCardFromDeck()
                    if (card.isSideboard) {
                        presenter.moveCardFromSideBoard(deck, card, card.quantity)
                    } else {
                        presenter.moveCardToSideBoard(deck, card, card.quantity)
                    }
                }
            }

        }
        cardList.adapter = deckAdapter

        mtgApp.uiGraph.inject(this)
        presenter.init(this)
        presenter.loadDeck(deck)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.deck, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        if (id == R.id.action_export) {
            exportDeck()
            return true

        } else if (id == R.id.action_edit) {
            editDeckName()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getPageTrack(): String? {
        return "/deck"
    }

    override fun deckLoaded(deckCollection: DeckCollection) {
        LOG.d(deckCollection.toCardsCollection().list.toString())
        loader.visibility = View.GONE
        val sections = ArrayList<DeckSection>()
        if (deckCollection.size() == 0) {
            emptyView.visibility = View.VISIBLE
            title = deck.name
        } else {
            sections.add(DeckSection(getString(R.string.deck_header_creatures) + " (" + deckCollection.getNumberOfCreatures() + ")", deckCollection.creatures))
            sections.add(DeckSection(getString(R.string.deck_header_instant_sorceries) + " (" + deckCollection.getNumberOfInstantAndSorceries() + ")", deckCollection.instantAndSorceries))
            sections.add(DeckSection(getString(R.string.deck_header_other) + " (" + deckCollection.getNumberOfOther() + ")", deckCollection.other))
            sections.add(DeckSection(getString(R.string.deck_header_lands) + " (" + deckCollection.getNumberOfLands() + ")", deckCollection.lands))
            sections.add(DeckSection(getString(R.string.deck_header_sideboard) + " (" + deckCollection.numberOfCardsInSideboard() + ")", deckCollection.side))
            title = deck.name + " (" + deckCollection.numberOfCardsWithoutSideboard() + "/" + deckCollection.numberOfCardsInSideboard() + ")"

        }
        deckAdapter.setSections(sections)
    }

    override fun deckExported(success: Boolean) {
        if (success) {
            val snackbar = Snackbar
                    .make(container, getString(R.string.deck_exported), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.share)) {
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(FileUtil.fileNameForDeck(deck)))
                        startActivity(Intent.createChooser(intent, getString(R.string.share)))
                        TrackingManager.trackDeckExport()
                    }
            snackbar.show()
        } else {
            exportDeckNotAllowed()
        }
    }

    private fun exportDeck() {
        LOG.d()
        requestPermission(PermissionUtil.TYPE.WRITE_STORAGE, object : PermissionUtil.PermissionListener {
            override fun permissionGranted() {
                presenter.exportDeck(deck, CardsCollection(deckAdapter.getCards(), null, false))
            }

            override fun permissionNotGranted() {
                exportDeckNotAllowed()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun exportDeckNotAllowed() {
        Toast.makeText(this, getString(R.string.error_export_deck), Toast.LENGTH_SHORT).show()
        TrackingManager.trackDeckExportError()
    }

    private fun editDeckName() {
        LOG.d()
        val alert = AlertDialog.Builder(this, R.style.MTGDialogTheme)

        alert.setTitle(getString(R.string.edit_deck))

        val layoutInflater = LayoutInflater.from(this)
        @SuppressLint("InflateParams") val view = layoutInflater.inflate(R.layout.dialog_edit_deck, null)
        val editText = view.findViewById<EditText>(R.id.edit_text)
        editText.setText(deck.name)
        editText.setSelection(deck.name.length)
        alert.setView(view)

        alert.setPositiveButton(getString(R.string.save)) { _, _ ->
            val value = editText.text.toString()
            presenter.editDeck(deck, value)
            TrackingManager.trackEditDeck()
            deck.name = value
            title = deck.name
        }

        alert.setNegativeButton(getString(R.string.cancel)) { _, _ ->
            // Canceled.
        }

        alert.show()
    }
}
