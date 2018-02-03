package com.dbottillo.mtgsearchfree.ui.decks

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.interactors.DecksInteractor
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject
import kotlin.math.min

class DeckStartingHandFragment : BasicFragment(), StartingHandView {

    @Inject
    lateinit var presenter: StartingHandPresenter

    lateinit var grid: RecyclerView
    private var adapter: StartingHandGridAdapter? = null

    private val numberOfColumns: Int by lazy { resources.getInteger(R.integer.starting_hand_grid_column_count) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.uiGraph.inject(this)
        presenter.init(this, arguments)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_starting_hand, container, false)
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        super.onViewCreated(view, bundle)

        grid = view.findViewById(R.id.starting_hand_grid)

        view.findViewById<View>(R.id.new_hand).setOnClickListener {
            presenter.repeat()
        }

        val glm = GridLayoutManager(context, numberOfColumns)
        glm.initialPrefetchItemCount = numberOfColumns
        grid.addItemDecoration(GridItemDecorator(resources.getDimensionPixelSize(R.dimen.cards_grid_space)))
        grid.setHasFixedSize(true)
        grid.layoutManager = glm

        presenter.loadDeck(bundle)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(BUNDLE_KEY_LEFT, ArrayList(presenter.cards))
        outState.putParcelableArrayList(BUNDLE_KEY_SHOWN, adapter?.cards?.let { ArrayList(it) })
    }

    override fun clear() {
        adapter?.cards?.clear()
        adapter?.notifyDataSetChanged()
    }

    override fun showOpeningHands(cards: MutableList<StartingHandCard>) {
        if (adapter == null) {
            adapter = StartingHandGridAdapter(cards, 3) {
                presenter.next()
            }
            grid.adapter = adapter
        } else {
            adapter?.cards = cards
            adapter?.notifyDataSetChanged()
        }
    }

    override fun newCard(cards: StartingHandCard) {
        adapter?.add(cards)
    }

    override fun getPageTrack(): String = "/starting_hand"

    override fun getTitle(): String = ""

    class GridItemDecorator(private val space: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View,
                                    parent: RecyclerView, state: RecyclerView.State?) {
            outRect.left = space / 2
            outRect.right = space / 2
            outRect.bottom = space / 2
            outRect.top = space
        }
    }
}

const val BUNDLE_KEY_SHOWN = "BUNDLE_KEY_SHOWN"
const val BUNDLE_KEY_LEFT = "BUNDLE_KEY_LEFT"

class StartingHandPresenter @Inject constructor(private val interactor: DecksInteractor) {

    lateinit var view: StartingHandView
    lateinit var deck: Deck
    lateinit var cards: MutableList<StartingHandCard>

    fun init(view: StartingHandView, arguments: Bundle?) {
        this.view = view
        deck = arguments?.get(DECK_KEY) as Deck
    }

    fun loadDeck(bundle: Bundle?) {
        bundle?.let {
            val array = bundle.getParcelableArrayList<StartingHandCard>(BUNDLE_KEY_LEFT)
            val shown = bundle.getParcelableArrayList<StartingHandCard>(BUNDLE_KEY_SHOWN)
            if (shown.isNotEmpty() && array.isNotEmpty()) {
                view.showOpeningHands(shown)
                cards = array
            } else {
                loadDeck()
            }
        } ?: loadDeck()
    }

    private fun loadDeck() {
        interactor.loadDeck(deck).subscribe({
            cards = mutableListOf()
            it.allCards()
                    .filter { !it.isSideboard }
                    .forEach { card ->
                        (1..card.quantity).forEach {
                            cards.add(StartingHandCard(card.mtgCardsInfoImage, card.gathererImage, card.name))
                        }
                    }
            cards.shuffle()
            newStartingHand()
        })
    }

    fun repeat() {
        view.clear()
        loadDeck()
    }

    fun next() {
        if (cards.isNotEmpty()) {
            view.newCard(cards.removeAt(0))
        }
    }

    private fun newStartingHand() {
        val initial: MutableList<StartingHandCard> = mutableListOf()
        (1..min(7, cards.size)).forEach {
            initial.add(cards.removeAt(0))
        }
        view.showOpeningHands(initial)
    }

}

interface StartingHandView {
    fun showOpeningHands(cards: MutableList<StartingHandCard>)
    fun newCard(cards: StartingHandCard)
    fun clear()
}

@SuppressLint("ParcelCreator")
@Parcelize
data class StartingHandCard(val mtgCardsInfoImage: String, val gathererImage: String, val name: String) : Parcelable