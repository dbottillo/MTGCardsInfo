package com.dbottillo.mtgsearchfree.ui.lucky

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.CommonCardsActivity
import com.dbottillo.mtgsearchfree.ui.decks.AddToDeckFragment
import com.dbottillo.mtgsearchfree.ui.views.MTGCardView
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.goToParentActivity
import com.dbottillo.mtgsearchfree.util.prefetchImage
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
        toolbar.elevation = 0f
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        mtgApp.uiGraph.inject(this)
        presenter.init(this, bundle, intent)
        cardView.setOnClickListener { presenter.showNextCard() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        presenter.init(this, null, intent)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenter.onSaveInstanceState(outState)
    }

    override fun getPageTrack(): String {
        return "/lucky-card"
    }

    override fun preFetchCardImage(card: MTGCard) {
        card.prefetchImage(this)
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
        return when (item.itemId) {
            R.id.action_add_to_deck -> {
                cardView.card?.let { openDialog("add_to_deck", AddToDeckFragment.newInstance(it)) }
                true
            }
            android.R.id.home -> {
                this.goToParentActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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