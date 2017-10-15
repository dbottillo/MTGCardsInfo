package com.dbottillo.mtgsearchfree.ui.lucky

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.CommonCardsActivity
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.MaterialWrapper
import com.dbottillo.mtgsearchfree.ui.decks.AddToDeckFragment
import com.dbottillo.mtgsearchfree.ui.views.MTGCardView
import com.squareup.picasso.Picasso
import javax.inject.Inject

class CardLuckyActivity : CommonCardsActivity(), CardsLuckyView {

    @Inject
    lateinit var presenter: CardsLuckyPresenter

    lateinit var cardView: MTGCardView
    lateinit var titleCard: TextView

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_lucky_card)

        setTitle(R.string.lucky_title)

        cardView = findViewById<MTGCardView>(R.id.card_view)
        titleCard = findViewById<TextView>(R.id.title_card)

        findViewById<View>(R.id.lucky_again).setOnClickListener { presenter.showNextCard() }

        setupToolbar()
        MaterialWrapper.setElevation(toolbar, 0f)
        supportActionBar?.let{
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        mtgApp.uiGraph.inject(this)
        presenter.init(this, bundle, intent)
        cardView.setOnClickListener { presenter.showNextCard() }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.onSaveInstanceState(outState)
    }

    override fun getPageTrack(): String {
        return "/lucky-card"
    }

    override fun preFetchCardImage(card: MTGCard) {
        Picasso.with(applicationContext).load(card.image).fetch()
    }

    override fun showCard(card: MTGCard, showImage: Boolean) {
        cardView.load(card, showImage)
        titleCard.text = card.name
        syncMenu()
    }

    public override fun getCurrentCard(): MTGCard? {
        LOG.d()
        return cardView.card
    }

    public override fun toggleImage(show: Boolean) {
        LOG.d()
        cardView.toggleImage(show)
    }

    public override fun favClicked() {
        LOG.d()
        presenter.saveOrRemoveCard()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lucky_card, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_add_to_deck) {
            cardView.card?.let { openDialog("add_to_deck", AddToDeckFragment.newInstance(it))  }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun syncMenu() {
        presenter.updateMenu()
    }

    override fun showFavMenuItem() {
        favMenuItem?.isVisible = true
    }

    override fun updateFavMenuItem(text: Int, icon: Int) {
        favMenuItem?.title = getString(text)
        favMenuItem?.setIcon(icon)
    }

    override fun hideFavMenuItem() {
        favMenuItem?.isVisible = false
    }

    override fun setImageMenuItemChecked(checked: Boolean) {
        imageMenuItem?.isChecked = checked
    }
}