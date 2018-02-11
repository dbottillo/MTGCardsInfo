package com.dbottillo.mtgsearchfree.ui.decks

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.DeckCollection
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import com.dbottillo.mtgsearchfree.ui.cards.CardsActivity
import com.dbottillo.mtgsearchfree.ui.cards.startCardsActivity
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.Logger
import com.dbottillo.mtgsearchfree.util.TrackingManager
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class DeckFragment : BasicFragment(), DeckView {

    lateinit var cardList: RecyclerView

    @Inject lateinit var presenter: DeckPresenter

    private val deckAdapter = DeckAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.uiGraph.inject(this)
        presenter.init(this, arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_deck, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardList = view.findViewById(R.id.card_list)

        deckAdapter.cardListener = object : OnDeckCardListener {
            override fun onCardSelected(card: MTGCard) {
                startActivity(view.context.startCardsActivity(presenter.deck, deckAdapter.getCards().indexOf(card)))
            }

            override fun onOptionSelected(menuItem: MenuItem, card: MTGCard) {
                if (menuItem.itemId == R.id.action_add_one_more) {
                    TrackingManager.trackAddCardToDeck()
                    presenter.addCardToDeck(card, 1)

                } else if (menuItem.itemId == R.id.action_remove_one) {
                    TrackingManager.trackRemoveCardFromDeck()
                    presenter.removeCardFromDeck(card)

                } else if (menuItem.itemId == R.id.action_remove_all) {
                    TrackingManager.trackRemoveAllCardsFromDeck()
                    presenter.removeAllCardFromDeck(card)

                } else if (menuItem.itemId == R.id.action_move_one) {
                    TrackingManager.trackMoveOneCardFromDeck()
                    if (card.isSideboard) {
                        presenter.moveCardFromSideBoard(card, 1)
                    } else {
                        presenter.moveCardToSideBoard(card, 1)
                    }

                } else if (menuItem.itemId == R.id.action_move_all) {
                    TrackingManager.trackMoveAllCardFromDeck()
                    if (card.isSideboard) {
                        presenter.moveCardFromSideBoard(card, card.quantity)
                    } else {
                        presenter.moveCardToSideBoard(card, card.quantity)
                    }
                }
            }

        }
        cardList.adapter = deckAdapter

        cardList.setHasFixedSize(true)
        cardList.layoutManager = LinearLayoutManager(view.context)

        presenter.loadDeck()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroyView()
    }

    override fun deckLoaded(title: String, collection: DeckCollection) {
        LOG.d(collection.toCardsCollection().list.toString())
        val sections = ArrayList<DeckSection>()
        sections.add(DeckSection(getString(R.string.deck_header_creatures) + " (" + collection.getNumberOfCreatures() + ")", collection.creatures))
        sections.add(DeckSection(getString(R.string.deck_header_instant_sorceries) + " (" + collection.getNumberOfInstantAndSorceries() + ")", collection.instantAndSorceries))
        sections.add(DeckSection(getString(R.string.deck_header_other) + " (" + collection.getNumberOfOther() + ")", collection.other))
        sections.add(DeckSection(getString(R.string.deck_header_lands) + " (" + collection.getNumberOfLands() + ")", collection.lands))
        sections.add(DeckSection(getString(R.string.deck_header_sideboard) + " (" + collection.numberOfCardsInSideboard() + ")", collection.side))

        deckAdapter.setSections(sections)
        activity?.title = title
    }

    override fun getPageTrack(): String = "/deck"

    override fun getTitle(): String = presenter.deck.name

}

class DeckPresenter @Inject constructor(private val interactor: DecksInteractor,
                                        private val logger: Logger) {

    lateinit var view: DeckView
    lateinit var deck: Deck
    private var disposable: CompositeDisposable = CompositeDisposable()

    fun init(view: DeckView, arguments: Bundle?) {
        this.view = view
        deck = arguments?.get(DECK_KEY) as Deck
    }

    fun loadDeck() {
        disposable.add(interactor.loadDeck(deck).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun addCardToDeck(card: MTGCard, quantity: Int) {
        disposable.add(interactor.addCard(deck, card, quantity).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun removeCardFromDeck(card: MTGCard) {
        disposable.add(interactor.removeCard(deck, card).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun removeAllCardFromDeck(card: MTGCard) {
        disposable.add(interactor.removeAllCard(deck, card).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun moveCardFromSideBoard(card: MTGCard, quantity: Int) {
        disposable.add(interactor.moveCardFromSideboard(deck, card, quantity).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun moveCardToSideBoard(card: MTGCard, quantity: Int) {
        disposable.add(interactor.moveCardToSideboard(deck, card, quantity).subscribe({
            view.deckLoaded("${deck.name} (${it.numberOfCardsWithoutSideboard()}/${it.numberOfCardsInSideboard()})",it)
        }))
    }

    fun onDestroyView(){
        disposable.dispose()
    }

}

const val DECK_KEY = "deck"

interface DeckView {
    fun deckLoaded(title: String, collection: DeckCollection)
}