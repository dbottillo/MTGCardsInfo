package com.dbottillo.mtgsearchfree.ui.lucky

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.CommonCardsActivity
import com.dbottillo.mtgsearchfree.ui.views.CardPresenter
import com.dbottillo.mtgsearchfree.ui.views.MTGCardView
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.goToParentActivity
import com.dbottillo.mtgsearchfree.util.prefetchImage
import dagger.android.AndroidInjection
import javax.inject.Inject

class CardLuckyActivity : CommonCardsActivity(), CardsLuckyView {

    @Inject lateinit var presenter: CardsLuckyPresenter
    @Inject lateinit var cardsPresenter: CardPresenter

    private val cardView by lazy(LazyThreadSafetyMode.NONE) { findViewById<MTGCardView>(R.id.card_view) }
    private val titleCard by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.title_card) }

    override fun onCreate(bundle: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(bundle)
        setContentView(R.layout.activity_lucky_card)

        setTitle(R.string.lucky_title)

        findViewById<View>(R.id.lucky_again).setOnClickListener { presenter.showNextCard() }

        setupToolbar(R.id.toolbar)
        toolbar.elevation = 0f
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        presenter.init(this, bundle, intent)
        cardView.init(cardsPresenter)
        cardView.setOnClickListener { presenter.showNextCard() }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
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

    public override val currentCard: MTGCard?
        get() {
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
                cardView.card?.let { openDialog("add_to_deck", navigator.newAddToDeckFragment(it)) }
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

    override fun shareImage(bitmap: Bitmap) {
        currentCard?.let { presenter.shareImage(bitmap) }
    }

    override fun shareUri(uri: Uri) {
        shareUriArtwork(currentCard?.name ?: "", uri)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}