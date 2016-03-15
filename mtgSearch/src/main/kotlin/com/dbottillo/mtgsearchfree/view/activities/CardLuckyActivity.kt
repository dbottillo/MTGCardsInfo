package com.dbottillo.mtgsearchfree.view.activities

import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.bindView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.presenter.CardsPresenter
import com.dbottillo.mtgsearchfree.resources.CardsBucket
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.view.CardsView
import com.dbottillo.mtgsearchfree.view.fragments.BasicFragment
import com.dbottillo.mtgsearchfree.view.views.MTGCardView
import com.squareup.picasso.Picasso
import java.util.*
import javax.inject.Inject

class CardLuckyActivity : CommonCardsActivity(), CardsView {

    companion object {
        val CARD = "CARD"
        val LUCKY_BATCH_CARDS = 10
    }

    private var luckyCards: ArrayList<MTGCard>? = null

    @Inject lateinit var cardsPresenter: CardsPresenter

    val cardView: MTGCardView by bindView(R.id.card_view)

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_lucky_card)

        ButterKnife.bind(this)

        setupToolbar()
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MTGApp.Companion.dataGraph.inject(this)
        cardsPresenter.init(this)

        findViewById(R.id.btn_lucky_again)?.setOnClickListener({
            refreshCard()
        })
        cardView.setOnClickListener({
            refreshCard()
        })

        if (bundle == null) {
            luckyCards = ArrayList<MTGCard>()
        } else {
            luckyCards = bundle.getParcelableArrayList<MTGCard>("luckyCards")
        }

        cardsPresenter.loadIdFavourites()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("luckyCards", luckyCards)
    }

    override fun onDestroy() {
        super.onDestroy()
        cardsPresenter.detachView()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun getPageTrack(): String {
        return "/lucky-card"
    }

    override fun cardLoaded(bucket: CardsBucket) {
        throw UnsupportedOperationException()
    }

    override fun luckyCardsLoaded(cards: ArrayList<MTGCard>) {
        var firstRun = luckyCards?.size == 0
        for (card in cards) {
            luckyCards?.add(card)
            if (card.image != null) {
                // pre-fetch images
                Picasso.with(this).load(card.image).fetch()
            }
        }
        if (firstRun) {
            refreshCard()
        }
    }

    override fun favIdLoaded(favourites: IntArray) {
        idFavourites = favourites
        if (luckyCards?.size == 0) {
            if (intent.extras != null && intent.extras.getParcelable<Parcelable>(CARD) != null) {
                luckyCards?.add(intent.extras.getParcelable<Parcelable>(CARD) as MTGCard)
                refreshCard()
            } else {
                cardsPresenter.getLuckyCards(LUCKY_BATCH_CARDS)
            }
        } else {
            updateMenu()
        }
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun refreshCard() {
        if (luckyCards == null || luckyCards?.isEmpty()!!) {
            cardsPresenter.getLuckyCards(LUCKY_BATCH_CARDS)
            return
        }
        var card = luckyCards?.removeAt(0)!!
        var sharedPreferences = getSharedPreferences(MTGApp.PREFS_NAME, 0)
        val showImage = sharedPreferences.getBoolean(BasicFragment.PREF_SHOW_IMAGE, true)
        cardView.load(card, showImage)
        if (luckyCards?.size!! <= 2) {
            cardsPresenter.getLuckyCards(LUCKY_BATCH_CARDS)
        }
        updateMenu()
    }

    override fun getCurrentCard(): MTGCard? {
        return cardView.card
    }

    override fun toggleImage(show: Boolean) {
        cardView.toggleImage(show)
    }

    override fun favClicked() {
        var currentCard = cardView.card!!
        if (idFavourites.contains(currentCard.multiVerseId)) {
            cardsPresenter.removeFromFavourite(currentCard)
        } else {
            cardsPresenter.saveAsFavourite(currentCard)
        }
    }

}
