package com.dbottillo.mtgsearchfree.decks.addToDeck

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.dbottillo.mtgsearchfree.decks.R
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_add_to_deck.*

@Suppress("EmptyFunctionBlock")
class AddToDeckFragment : BottomSheetDialogFragment(), AddToDeckView {
    private var decksChoose: MutableList<String> = mutableListOf()

    private var decks: List<Deck> = mutableListOf()
    private var quantity = 1

    @Inject
    lateinit var presenter: AddToDeckPresenter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_to_deck, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_to_deck_save.setOnClickListener { addToDeck() }

        setupQuantity()

        presenter.init(this, arguments)
    }

    override fun onResume() {
        super.onResume()
        TrackingManager.trackPage("/add_to_deck")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroyView()
    }

    private fun setupQuantity() {
        LOG.d()
        quantity_plus.setOnClickListener {
            quantity++
            updateQuantityIndicator()
        }
        quantity_minus.setOnClickListener {
            quantity--
            if (quantity < 1) {
                quantity = 1
            }
            updateQuantityIndicator()
        }
        updateQuantityIndicator()
    }

    private fun updateQuantityIndicator() {
        quantity_indicator.text = quantity.toString()
    }

    private fun setupDecksSpinner(decks: List<Deck>, selectedDeck: Long) {
        LOG.d()
        this.decks = decks
        decksChoose.clear()
        decksChoose.add(getString(R.string.deck_choose))
        decks.forEach { decksChoose.add(it.name) }
        decksChoose.add(getString(R.string.deck_new))
        val adapter = ArrayAdapter<CharSequence>(activity as FragmentActivity, R.layout.add_to_deck_spinner_item, decksChoose.toTypedArray())
        adapter.setDropDownViewResource(R.layout.add_to_deck_dropdown_item)
        choose_deck.adapter = adapter
        choose_deck.setSelection(decks.indexOf(decks.find { it.id == selectedDeck }) + 1)
        choose_deck.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position == decks.size + 1) {
                    choose_deck.visibility = View.GONE
                    new_deck_name_input_layout.visibility = View.VISIBLE
                    new_deck_name.requestFocus()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun addToDeck() {
        LOG.d()
        if (choose_deck.visibility == View.VISIBLE && choose_deck.selectedItemPosition > 0) {
            val deck = decks[choose_deck.selectedItemPosition - 1]
            val side = add_to_deck_sideboard.isChecked
            saveCard(quantity, deck, side)
            dismiss()
        }
        if (choose_deck.visibility == View.GONE && new_deck_name.text?.isNotEmpty() == true) {
            val side = add_to_deck_sideboard.isChecked
            saveCard(quantity, new_deck_name.text.toString(), side)
            dismiss()
        }
    }

    private fun saveCard(quantity: Int, deck: Deck, side: Boolean) {
        LOG.d()
        presenter.addCardToDeck(deck, quantity, side)
        TrackingManager.trackAddCardToDeck("$quantity - existing")
    }

    private fun saveCard(quantity: Int, deck: String, side: Boolean) {
        LOG.d()
        presenter.addCardToDeck(deck, quantity, side)
        TrackingManager.trackNewDeck(deck)
        TrackingManager.trackAddCardToDeck("$quantity - existing")
    }

    override fun showError(message: String) {
        add_card_title.visibility = View.GONE
        new_deck_name_input_layout.visibility = View.GONE
        quantity_indicator.visibility = View.GONE
        quantity_container.visibility = View.GONE
        quantity_minus.visibility = View.GONE
        quantity_plus.visibility = View.GONE
        choose_deck.visibility = View.GONE
        add_to_deck_sideboard.visibility = View.GONE
        error_text.visibility = View.VISIBLE
        add_to_deck_save.setText(android.R.string.ok)
        add_to_deck_save.setOnClickListener { this.dismiss() }
    }

    override fun setCardTitle(cardName: String) {
        add_card_title.text = getString(R.string.add_to_deck_title, cardName)
    }

    override fun decksLoaded(decks: List<Deck>, selectedDeck: Long) {
        LOG.d()
        setupDecksSpinner(decks, selectedDeck)
    }

    companion object {

        fun newInstance(card: MTGCard): DialogFragment {
            val instance = AddToDeckFragment()
            val args = Bundle()
            args.putInt("card", card.multiVerseId)
            args.putString("cardName", card.name)
            instance.arguments = args
            return instance
        }
    }
}
