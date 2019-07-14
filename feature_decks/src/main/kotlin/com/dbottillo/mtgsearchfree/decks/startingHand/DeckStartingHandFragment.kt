package com.dbottillo.mtgsearchfree.decks.startingHand

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbottillo.mtgsearchfree.decks.deck.DECK_KEY
import com.dbottillo.mtgsearchfree.decks.R
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class DeckStartingHandFragment : BasicFragment(), StartingHandView {

    @Inject
    lateinit var presenter: StartingHandPresenter

    lateinit var grid: RecyclerView
    private var adapter: StartingHandGridAdapter? = null

    private val numberOfColumns: Int by lazy { resources.getInteger(R.integer.starting_hand_grid_column_count) }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.init(this, arguments?.getLong(DECK_KEY, 0) ?: 0)
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

        val array = bundle?.getParcelableArrayList<StartingHandCard>(BUNDLE_KEY_LEFT)
        val shown = bundle?.getParcelableArrayList<StartingHandCard>(BUNDLE_KEY_SHOWN)
        presenter.loadDeck(Pair(array, shown))
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

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.left = space / 2
            outRect.right = space / 2
            outRect.bottom = space / 2
            outRect.top = space
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroyView()
    }
}

const val BUNDLE_KEY_SHOWN = "BUNDLE_KEY_SHOWN"
const val BUNDLE_KEY_LEFT = "BUNDLE_KEY_LEFT"

@SuppressLint("ParcelCreator")
@Parcelize
data class StartingHandCard(val gathererImage: String, val name: String) : Parcelable