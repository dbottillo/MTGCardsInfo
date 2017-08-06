package com.dbottillo.mtgsearchfree.ui.decks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.dbottillo.mtgsearchfree.util.FileUtil
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.PermissionUtil
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.ui.cards.OnCardListener
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar
import java.util.*
import javax.inject.Inject

class DeckActivity : BasicActivity(), DeckActivityView {

    @Inject
    lateinit var presenter: DeckActivityPresenter

    lateinit var container: View
    lateinit var emptyView: TextView
    lateinit var progressBar: SmoothProgressBar
    lateinit var cardList: RecyclerView

    lateinit var deck: Deck

    private var cards: MutableList<MTGCard> = mutableListOf()
    private var deckCardSectionAdapter: DeckCardSectionAdapter? = null

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_deck)

        container = findViewById(R.id.container)
        emptyView = findViewById<TextView>(R.id.empty_view)
        progressBar = findViewById<SmoothProgressBar>(R.id.progress)
        cardList = findViewById<RecyclerView>(R.id.card_list)

        setupToolbar()

        deck = intent.getParcelableExtra<Deck>("deck")
        title = if (deck.name == null) getString(R.string.deck_title) else deck.name

        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        emptyView.setText(R.string.empty_deck)
        
        cardList.setHasFixedSize(true)
        cardList.layoutManager = LinearLayoutManager(this)

        val deckCardAdapter = DeckCardAdapter(this, cards, R.menu.deck_card, object : OnCardListener {
            override fun onCardsHeaderSelected() {

            }

            override fun onCardsViewTypeSelected() {

            }

            override fun onCardsSettingSelected() {

            }

            override fun onCardSelected(card: MTGCard, position: Int) {
                startActivity(CardsActivity.newInstance(this@DeckActivity, deck, cardPositionWithoutSections(card)))
            }

            override fun onOptionSelected(menuItem: MenuItem, card: MTGCard, position: Int) {
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
        })
        deckCardSectionAdapter = DeckCardSectionAdapter(this, deckCardAdapter)
        cardList.adapter = deckCardSectionAdapter

        mtgApp.uiGraph.inject(this)
        presenter.init(this)
        presenter.loadDeck(deck)
    }

    private fun cardPositionWithoutSections(card: MTGCard): Int {
        val positionWithoutSections = cards.indices.firstOrNull { cards[it] == card }
                ?: 0
        return positionWithoutSections
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
        LOG.d()
        progressBar.visibility = View.GONE
        val sections = ArrayList<DeckCardSectionAdapter.Section>()
        cards.clear()
        if (deckCollection.size() == 0) {
            emptyView.visibility = View.VISIBLE
            title = deck.name
        } else {
            var startingPoint = 0
            if (deckCollection.getNumberOfUniqueCreatures() > 0) {
                sections.add(DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_creatures) + " (" + deckCollection.getNumberOfCreatures() + ")"))
                startingPoint += deckCollection.getNumberOfUniqueCreatures()
                cards.addAll(deckCollection.creatures)
            }
            if (deckCollection.getNumberOfUniqueInstantAndSorceries() > 0) {
                sections.add(DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_instant_sorceries) + " (" + deckCollection.getNumberOfInstantAndSorceries() + ")"))
                startingPoint += deckCollection.getNumberOfUniqueInstantAndSorceries()
                cards.addAll(deckCollection.instantAndSorceries)
            }
            if (deckCollection.getNumberOfUniqueOther() > 0) {
                sections.add(DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_other) + " (" + deckCollection.getNumberOfOther() + ")"))
                startingPoint += deckCollection.getNumberOfUniqueOther()
                cards.addAll(deckCollection.other)
            }
            if (deckCollection.getNumberOfUniqueLands() > 0) {
                sections.add(DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_lands) + " (" + deckCollection.getNumberOfLands() + ")"))
                startingPoint += deckCollection.getNumberOfUniqueLands()
                cards.addAll(deckCollection.lands)
            }
            if (deckCollection.numberOfUniqueCardsInSideboard() > 0) {
                sections.add(DeckCardSectionAdapter.Section(startingPoint, getString(R.string.deck_header_sideboard) + " (" + deckCollection.numberOfCardsInSideboard() + ")"))
                cards.addAll(deckCollection.side)
            }

            title = deck.name + " (" + deckCollection.numberOfCardsWithoutSideboard() + "/" + deckCollection.numberOfCardsInSideboard() + ")"

        }
        deckCardSectionAdapter?.setSections(sections.toTypedArray())
        deckCardSectionAdapter?.notifyDataSetChanged()
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

   /* override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showError(exception: MTGException) {

    }*/

    private fun exportDeck() {
        LOG.d()
        requestPermission(PermissionUtil.TYPE.WRITE_STORAGE, object : PermissionUtil.PermissionListener {
            override fun permissionGranted() {
                presenter.exportDeck(deck, CardsCollection(cards, null, false))
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
