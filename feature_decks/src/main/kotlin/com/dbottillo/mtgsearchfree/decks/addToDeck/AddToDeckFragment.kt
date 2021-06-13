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
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatEditText
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
    private var decksChoose: MutableList<String> = mutableListOf()

    private var decks: List<Deck> = mutableListOf()
    private var quantity = 1

    @Inject lateinit var presenter: AddToDeckPresenter
    @Inject lateinit var trackingManager: TrackingManager

    private lateinit var addToDeckSave: Button
    private lateinit var quantityPlus: Button
    private lateinit var quantityMinus: Button
    private lateinit var quantityIndicator: TextView
    private lateinit var chooseDeck: Spinner
    private lateinit var newDeckInputLayout: View
    private lateinit var addCardTitle: TextView
    private lateinit var quantityContainer: View
    private lateinit var addToDeckSideboard: AppCompatCheckBox
    private lateinit var newDeckName: AppCompatEditText
    private lateinit var errorText: TextView

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_to_deck, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addToDeckSave = view.findViewById(R.id.add_to_deck_save)
        quantityPlus = view.findViewById(R.id.quantity_plus)
        quantityMinus = view.findViewById(R.id.quantity_minus)
        quantityIndicator = view.findViewById(R.id.quantity_indicator)
        chooseDeck = view.findViewById(R.id.choose_deck)
        newDeckInputLayout = view.findViewById(R.id.new_deck_name_input_layout)
        addCardTitle = view.findViewById(R.id.add_card_title)
        quantityContainer = view.findViewById(R.id.quantity_container)
        addToDeckSideboard = view.findViewById(R.id.add_to_deck_sideboard)
        newDeckName = view.findViewById(R.id.new_deck_name)
        errorText = view.findViewById(R.id.error_text)

        addToDeckSave.setOnClickListener { addToDeck() }

        setupQuantity()

        presenter.init(this, arguments)
    }

    override fun onResume() {
        super.onResume()
        trackingManager.trackPage("/add_to_deck")
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
                    newDeckInputLayout.visibility = View.VISIBLE
                    newDeckName.requestFocus()
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
            val side = addToDeckSideboard.isChecked
            saveCard(quantity, deck, side)
            dismiss()
        }
        if (chooseDeck.visibility == View.GONE && newDeckName.text?.isNotEmpty() == true) {
            val side = addToDeckSideboard.isChecked
            saveCard(quantity, newDeckName.text.toString(), side)
            dismiss()
        }
    }

    private fun saveCard(quantity: Int, deck: Deck, side: Boolean) {
        LOG.d()
        presenter.addCardToDeck(deck, quantity, side)
        trackingManager.trackAddCardToDeck("$quantity - existing")
    }

    private fun saveCard(quantity: Int, deck: String, side: Boolean) {
        LOG.d()
        presenter.addCardToDeck(deck, quantity, side)
        trackingManager.trackNewDeck(deck)
        trackingManager.trackAddCardToDeck("$quantity - existing")
    }

    override fun showError(message: String) {
        addCardTitle.visibility = View.GONE
        newDeckInputLayout.visibility = View.GONE
        quantityIndicator.visibility = View.GONE
        quantityContainer.visibility = View.GONE
        quantityMinus.visibility = View.GONE
        quantityPlus.visibility = View.GONE
        chooseDeck.visibility = View.GONE
        addToDeckSideboard.visibility = View.GONE
        errorText.visibility = View.VISIBLE
        addToDeckSave.setText(android.R.string.ok)
        addToDeckSave.setOnClickListener { this.dismiss() }
    }

    override fun setCardTitle(cardName: String) {
        addCardTitle.text = getString(R.string.add_to_deck_title, cardName)
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
