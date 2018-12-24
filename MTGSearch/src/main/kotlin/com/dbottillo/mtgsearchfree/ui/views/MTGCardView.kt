package com.dbottillo.mtgsearchfree.ui.views

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.TCGPrice
import com.dbottillo.mtgsearchfree.model.network.NetworkIntentService
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.util.addBold
import com.dbottillo.mtgsearchfree.util.boldTitledEntry
import com.dbottillo.mtgsearchfree.util.calculateSizeCardImage
import com.dbottillo.mtgsearchfree.util.loadInto
import com.dbottillo.mtgsearchfree.util.newLine
import com.dbottillo.mtgsearchfree.util.setBoldAndItalic

class MTGCardView(context: Context, attrs: AttributeSet?, defStyle: Int) : RelativeLayout(context, attrs, defStyle), CardView {

    private var detailCard: TextView
    private var priceOnTcg: TextView
    private var cardPrice: TextView
    private var retry: View
    private var cardImage: ImageView
    private var cardLoader: MTGLoader
    private var cardImageContainer: View
    private var flipCardButton: ImageButton
    private var isLandscape = false

    var card: MTGCard? = null
    internal var price: TCGPrice? = null

    private lateinit var cardPresenter: CardPresenter

    @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : this(ctx, attrs, -1) {}

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_mtg_card, this, true)

        detailCard = view.findViewById(R.id.detail_card)
        priceOnTcg = view.findViewById(R.id.price_on_tcg)
        cardPrice = view.findViewById(R.id.price_card)
        retry = view.findViewById(R.id.image_card_retry)
        cardImage = view.findViewById(R.id.image_card)
        cardLoader = view.findViewById(R.id.image_card_loader)
        cardImageContainer = view.findViewById(R.id.image_card_container)
        flipCardButton = view.findViewById(R.id.card_flip)

        view.findViewById<View>(R.id.image_card_retry_btn).setOnClickListener { retryImage() }
        view.findViewById<View>(R.id.price_container).setOnClickListener { openPrice() }
        flipCardButton.setOnClickListener { flipCard() }

        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)

        val paddingCard = resources.getDimensionPixelSize(R.dimen.padding_card_image)
        var widthAvailable = size.x - paddingCard * 2
        if (isLandscape) {
            widthAvailable = size.x / 2 - paddingCard * 2
        }
        cardImage.calculateSizeCardImage(widthAvailable, resources.getBoolean(R.bool.isTablet))
        priceOnTcg.setBoldAndItalic("TCG")
    }

    fun init(cardPresenter: CardPresenter) {
        this.cardPresenter = cardPresenter
        cardPresenter.init(this)
    }

    private val priceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            price = intent.getParcelableExtra(NetworkIntentService.REST_RESULT)
            updatePrice()
            if (price != null && price!!.isNotFound && isNetworkAvailable) {
                val url = intent.getStringExtra(NetworkIntentService.REST_URL)
                TrackingManager.trackPriceError(url)
            }
        }
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LOG.d()
        LocalBroadcastManager.getInstance(context).unregisterReceiver(priceReceiver)
    }

    internal var showImage: Boolean = false

    fun load(card: MTGCard, showImage: Boolean) {
        this.showImage = showImage
        load(card)
    }

    private fun load(card: MTGCard) {
        LOG.d()
        this.card = card
        val resources = context.resources

        detailCard.text = SpannableStringBuilder().apply {
            boldTitledEntry(resources.getString(R.string.card_detail_type), card.type)
            boldTitledEntry(resources.getString(R.string.card_detail_pt), "${card.power} / ${card.toughness}")
            boldTitledEntry(resources.getString(R.string.card_detail_mana), if (card.manaCost.isNotEmpty()) "${card.manaCost} (${card.cmc})" else " - ")
            append(card.text).newLine(2)
            card.set?.let { boldTitledEntry(resources.getString(R.string.card_detail_set), it.name) }
            boldTitledEntry(resources.getString(R.string.card_detail_original_text), card.originalText)
            if (card.rulings.isNotEmpty()) {
                addBold(resources.getString(R.string.card_detail_rulings))
                append(":").newLine()
                card.rulings.forEach {
                    append("-").append(" ").append(it).newLine()
                }
                newLine()
            }
            if (card.legalities.isNotEmpty()) {
                addBold(resources.getString(R.string.card_detail_legalities))
                append(":").newLine()
                card.legalities.forEach {
                    append("-").append(" ").append(it.format).append(": ").append(it.legality).newLine()
                }
            }
        }

        val intent = Intent(context, NetworkIntentService::class.java)
        val params = Bundle()
        params.putString(NetworkIntentService.EXTRA_ID, card.multiVerseId.toString() + "")
        params.putString(NetworkIntentService.EXTRA_CARD_NAME, card.name)
        card.set?.let { params.putString(NetworkIntentService.EXTRA_SET_NAME, it.name) }
        intent.putExtra(NetworkIntentService.EXTRA_PARAMS, params)
        context.startService(intent)

        retry.visibility = View.GONE

        if (showImage) {
            loadImage()
        } else {
            cardLoader.visibility = View.GONE
            cardImageContainer.visibility = View.GONE
        }

        LocalBroadcastManager.getInstance(context).unregisterReceiver(priceReceiver)
        val cardFilter = IntentFilter(card.multiVerseId.toString() + "")
        LocalBroadcastManager.getInstance(context).registerReceiver(priceReceiver, cardFilter)

        flipCardButton.visibility = if (card.isDoubleFaced) View.VISIBLE else View.GONE
    }

    private fun updatePrice() {
        LOG.d()
        price?.let {
            if (it.isAnError) {
                cardPrice.text = it.errorPrice
            } else {
                cardPrice.text = it.toDisplay(isLandscape)
            }
        }
    }

    private fun loadImage() {
        LOG.d()
        card?.loadInto(cardLoader, cardImage, retry)
    }

    private val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    private fun retryImage() {
        loadImage()
    }

    private fun openPrice() {
        LOG.d()
        price?.let {
            if (!it.isAnError) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.link))
                context.startActivity(browserIntent)
            }
        }
    }

    fun toggleImage(showImage: Boolean) {
        LOG.d()
        card?.let {
            if (showImage) {
                loadImage()
            } else {
                cardLoader.visibility = View.GONE
                cardImageContainer.visibility = View.GONE
            }
        }
    }

    private fun flipCard() {
        card?.let { cardPresenter.loadOtherSideCard(it) }
    }

    override fun otherSideCardLoaded(card: MTGCard) {
        load(card)
    }
}

const val RATIO_CARD = 1.39622641509434f