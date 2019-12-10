package com.dbottillo.mtgsearchfree.decks.addToDeck

import android.content.Context
import android.os.Bundle
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.dbottillo.mtgsearchfree.decks.R
import com.dbottillo.mtgsearchfree.model.Deck
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject
import dagger.android.support.AndroidSupportInjection

@Suppress("EmptyFunctionBlock")
class AddToDeckFragment : BottomSheetDialogFragment(), AddToDeckView {

    private lateinit var chooseDeck: Spinner
    private lateinit var sideboard: CheckBox
    private lateinit var cardNameInputLayout: TextInputLayout
    private lateinit var deckName: EditText
    private lateinit var title: TextView
    private lateinit var quantityIndicator: TextView
    private lateinit var quantityPlus: Button
    private lateinit var quantityMinus: Button

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

        title = view.findViewById(R.id.add_card_title)
        chooseDeck = view.findViewById(R.id.choose_deck)
        sideboard = view.findViewById(R.id.add_to_deck_sideboard)
        cardNameInputLayout = view.findViewById(R.id.new_deck_name_input_layout)
        deckName = view.findViewById(R.id.new_deck_name)
        quantityIndicator = view.findViewById(R.id.quantity_indicator)
        quantityPlus = view.findViewById(R.id.quantity_plus)
        quantityMinus = view.findViewById(R.id.quantity_minus)
        view.findViewById<View>(R.id.add_to_deck_save).setOnClickListener { addToDeck() }

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
        quantityPlus.setOnClickListener {
            quantity++
            updateQuantityIndicator()
        }
        quantityMinus.setOnClickListener {
            quantity--
            if (quantity < 1) {
                quantity = 1
            }
            updateQuantityIndicator()
        }
        updateQuantityIndicator()
    }

    private fun updateQuantityIndicator() {
        quantityIndicator.text = quantity.toString()
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
        chooseDeck.adapter = adapter
        chooseDeck.setSelection(decks.indexOf(decks.find { it.id == selectedDeck }) + 1)
        chooseDeck.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position == decks.size + 1) {
                    chooseDeck.visibility = View.GONE
                    cardNameInputLayout.visibility = View.VISIBLE
                    deckName.requestFocus()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun addToDeck() {
        LOG.d()
        if (chooseDeck.visibility == View.VISIBLE && chooseDeck.selectedItemPosition > 0) {
            val deck = decks[chooseDeck.selectedItemPosition - 1]
            val side = sideboard.isChecked
            saveCard(quantity, deck, side)
            dismiss()
        }
        if (chooseDeck.visibility == View.GONE && deckName.text.isNotEmpty()) {
            val side = sideboard.isChecked
            saveCard(quantity, deckName.text.toString(), side)
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun setCardTitle(cardName: String) {
        title.text = getString(R.string.add_to_deck_title, cardName)
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
            instance.arguments = args
            return instance
        }
    }
}
