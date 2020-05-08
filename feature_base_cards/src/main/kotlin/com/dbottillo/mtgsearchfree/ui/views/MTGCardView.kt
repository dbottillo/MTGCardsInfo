package com.dbottillo.mtgsearchfree.ui.views

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.net.Uri
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.dbottillo.mtgsearchfree.featurebasecards.R
import com.dbottillo.mtgsearchfree.model.MKMCardPrice
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.model.PriceProvider
import com.dbottillo.mtgsearchfree.model.PriceProvider.TCG
import com.dbottillo.mtgsearchfree.repository.CardPriceException
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.util.TrackingManager
import com.dbottillo.mtgsearchfree.util.addBold
import com.dbottillo.mtgsearchfree.util.boldTitledEntry
import com.dbottillo.mtgsearchfree.util.calculateSizeCardImage
import com.dbottillo.mtgsearchfree.util.loadInto
import com.dbottillo.mtgsearchfree.util.newLine
import com.dbottillo.mtgsearchfree.util.setBoldAndItalic
import io.reactivex.disposables.Disposable

class MTGCardView(context: Context, attrs: AttributeSet?, defStyle: Int) :
    RelativeLayout(context, attrs, defStyle) {

    private var detailCard: TextView
    private var priceLink: TextView
    private var cardPrice: TextView
    private var retry: View
    private var cardImage: ImageView
    private var cardLoader: MTGLoader
    private var cardImageContainer: View
    private var flipCardButton: ImageButton
    private var isLandscape = false
    private var priceContainer: View

    var card: MTGCard? = null

    private lateinit var cardPresenter: CardPresenter

    private var disposable: Disposable? = null
    private var otherCardDisposable: Disposable? = null

    @JvmOverloads
    constructor(ctx: Context, attrs: AttributeSet? = null) : this(ctx, attrs, -1)

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_mtg_card, this, true)

        detailCard = view.findViewById(R.id.detail_card)
        priceLink = view.findViewById(R.id.price_link)
        cardPrice = view.findViewById(R.id.price_card)
        retry = view.findViewById(R.id.image_card_retry)
        cardImage = view.findViewById(R.id.image_card)
        cardLoader = view.findViewById(R.id.image_card_loader)
        cardImageContainer = view.findViewById(R.id.image_card_container)
        flipCardButton = view.findViewById(R.id.card_flip)
        priceContainer = view.findViewById(R.id.price_container)

        view.findViewById<View>(R.id.image_card_retry_btn).setOnClickListener { retryImage() }
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
    }

    fun init(cardPresenter: CardPresenter) {
        this.cardPresenter = cardPresenter
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LOG.d()
        disposable?.dispose()
        otherCardDisposable?.dispose()
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
            boldTitledEntry(
                resources.getString(R.string.card_detail_pt),
                "${card.power} / ${card.toughness}"
            )
            boldTitledEntry(
                resources.getString(R.string.card_detail_mana),
                if (card.manaCost.isNotEmpty()) "${card.manaCost} (${card.cmc})" else " - "
            )
            boldTitledEntry(
                resources.getString(R.string.card_detail_rarity),
                resources.getString(card.displayRarity)
            )
            append(card.text).newLine(2)
            card.set?.let {
                boldTitledEntry(
                    resources.getString(R.string.card_detail_set),
                    it.name
                )
            }
            if (card.originalText != card.text) {
                boldTitledEntry(
                    resources.getString(R.string.card_detail_original_text),
                    card.originalText
                )
            }
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
                    append("-").append(" ").append(it.format).append(": ").append(it.legality)
                        .newLine()
                }
            }
        }

        loadPrice(card)

        retry.visibility = View.GONE

        if (showImage) {
            loadImage()
        } else {
            cardLoader.visibility = View.GONE
            cardImageContainer.visibility = View.GONE
        }

        flipCardButton.visibility =
            if (card.isDoubleFaced || card.isTransform) View.VISIBLE else View.GONE
    }

    private fun loadPrice(card: MTGCard) {
        val priceProviderPref =
            PreferenceManager.getDefaultSharedPreferences(context).getString("price_provider", null)
        if (priceProviderPref == "MKM") {
            priceLink.setBoldAndItalic("MKM")
        } else {
            priceLink.setBoldAndItalic("TCG")
        }
        val priceProvider = if (priceProviderPref == "MKM") PriceProvider.MKM else TCG

        disposable = cardPresenter.fetchPrice(card, priceProvider).subscribe({ apiPrice ->
            if (apiPrice is MKMCardPrice) {
                cardPrice.text = context.getString(R.string.mkm_price, apiPrice.low, apiPrice.trend)
                priceContainer.setOnClickListener {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.cardmarket.com/${apiPrice.url}")
                        )
                    )
                }
                findViewById<View>(R.id.price_not_exact).visibility =
                    if (apiPrice.exact) View.GONE else View.VISIBLE
            } else {
                priceContainer.setOnClickListener { openPrice() }
                cardPrice.text = apiPrice.toDisplay(isLandscape)
            }
        }, {
            cardPrice.text = context.getText(R.string.price_error)
            if (it is CardPriceException) {
                TrackingManager.trackPriceError(it.toString())
            }
        })
    }

    private fun loadImage() {
        LOG.d()
        card?.loadInto(cardLoader, cardImage, retry)
    }

    private fun retryImage() {
        loadImage()
    }

    private fun openPrice() {
        LOG.d()
        var url = card?.tcgplayerPurchaseUrl
        if (url == null || url.isEmpty()) {
            url = "https://shop.tcgplayer.com/product/productsearch?id=${card?.tcgplayerProductId}"
        }
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
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
        card?.let {
            otherCardDisposable = cardPresenter.loadOtherSideCard(it).subscribe({ otherCard ->
                load(otherCard)
            }, { throwable ->
                Toast.makeText(context, throwable.localizedMessage, Toast.LENGTH_SHORT).show()
            })
        }
    }
}