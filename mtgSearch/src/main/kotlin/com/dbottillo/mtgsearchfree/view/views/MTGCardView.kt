package com.dbottillo.mtgsearchfree.view.views

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.AnimationDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.bindView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.helper.LOG
import com.dbottillo.mtgsearchfree.network.NetworkIntentService
import com.dbottillo.mtgsearchfree.resources.MTGCard
import com.dbottillo.mtgsearchfree.tracking.TrackingManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class MTGCardView : RelativeLayout {

    val mainContainer: View by bindView(R.id.fragment_card_container)
    val detailCard: TextView by bindView(R.id.detail_card)
    val priceOnTcg: TextView by bindView(R.id.price_on_tcg)
    val cardPrice: TextView by bindView(R.id.price_card)
    val retry: View by bindView(R.id.image_card_retry)
    val cardImage: ImageView by bindView(R.id.image_card)
    val cardLoader: ImageView by bindView(R.id.image_card_loader)
    val cardImageContainer: View by bindView(R.id.image_card_container)

    val RATIO_CARD = 1.39622641509434f
    var widthAvailable: Int = 0
    var heightAvailable: Int = 0
    var isLandscape: Boolean = false;
    var card: MTGCard? = null
    var price: TCGPrice? = null

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, -1)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.view_mtg_card, this, true)
        ButterKnife.bind(this, view)

        priceOnTcg.text = Html.fromHtml("<i><u>TCG</i></u>")

        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        mainContainer.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                var paddingCard = resources.getDimensionPixelSize(R.dimen.padding_card_image)
                widthAvailable = mainContainer.width - paddingCard * 2
                if (isLandscape) {
                    widthAvailable = mainContainer.width / 2 - paddingCard * 2
                }
                heightAvailable = mainContainer.height - paddingCard
                updateSizeImage()
                mainContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private val priceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            price = intent.getParcelableExtra<TCGPrice>(NetworkIntentService.REST_RESULT)
            LOG.e("price received: "+price.toString())
            updatePrice()
            if (price != null && price!!.isNotFound && isNetworkAvailable()) {
                val url = intent.getStringExtra(NetworkIntentService.REST_URL)
                TrackingManager.trackPriceError(url)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LocalBroadcastManager.getInstance(context).unregisterReceiver(priceReceiver)
    }

    fun load(card: MTGCard, showImage: Boolean) {
        this.card = card
        val manaCost: String
        val rulings: String
        if (card.manaCost != null) {
            manaCost = card.manaCost + " (" + card.cmc + ")"
        } else {
            manaCost = " - "
        }
        if (card.rulings.size > 0) {
            val html = StringBuilder()
            for (rule in card.rulings) {
                html.append("- ").append(rule).append("<br/><br/>")
            }
            rulings = html.toString()
        } else {
            rulings = ""
        }
        detailCard.text = Html.fromHtml(context.resources.getString(R.string.card_detail, card.type,
                card.power, card.toughness, manaCost, card.text, card.setName, rulings))

        val intent = Intent(context, NetworkIntentService::class.java)
        val params = Bundle()
        params.putString(NetworkIntentService.EXTRA_ID, card.multiVerseId.toString())
        params.putString(NetworkIntentService.EXTRA_CARD_NAME, card.name)
        params.putString(NetworkIntentService.EXTRA_SET_NAME, card.setName)
        intent.putExtra(NetworkIntentService.EXTRA_PARAMS, params)
        context.startService(intent)

        retry.visibility = View.GONE

        if (showImage && card.image != null) {
            loadImage(false)
        } else {
            cardLoader.visibility = View.GONE
            cardImageContainer.visibility = View.GONE
        }

        LocalBroadcastManager.getInstance(context).unregisterReceiver(priceReceiver)
        val cardFilter = IntentFilter(card.multiVerseId.toString())
        LocalBroadcastManager.getInstance(context).registerReceiver(priceReceiver, cardFilter)
    }

    private fun updatePrice() {
        if (price == null) {
            return;
        }
        if (price!!.isAnError) {
            cardPrice.text = price?.errorPrice
        } else {
            cardPrice.text = price?.toDisplay(isLandscape)
        }
    }

    private fun loadImage(fallback: Boolean) {
        retry.visibility = View.GONE
        cardImageContainer.visibility = View.VISIBLE
        cardImage.visibility = View.GONE
        startCardLoader()
        Picasso.with(context).load(if (fallback) card?.imageFromGatherer else card?.image).into(cardImage, object : Callback {
            override fun onSuccess() {
                cardImage.visibility = View.VISIBLE
                stopCardLoader()
            }

            override fun onError() {
                if (!fallback) {
                    // need to try to load from gatherer
                    loadImage(true)
                    return
                }
                stopCardLoader()
                cardImage.visibility = View.GONE
                retry.visibility = View.VISIBLE
                if (isNetworkAvailable()) {
                    TrackingManager.trackImageError(card?.image!!)
                }
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun startCardLoader() {
        cardLoader.visibility = View.VISIBLE
        val frameAnimation = cardLoader.background as AnimationDrawable
        cardLoader.post { frameAnimation.start() }
    }

    private fun stopCardLoader() {
        val frameAnimation = cardLoader.background as AnimationDrawable
        cardLoader.post {
            frameAnimation.stop()
            cardLoader.visibility = View.GONE
        }
    }

    fun updateSizeImage() {
        var ratioScreen = heightAvailable.toFloat() / widthAvailable.toFloat();
        // screen taller than magic card, we can take the width
        var wImage = widthAvailable;
        var hImage = (widthAvailable * RATIO_CARD).toInt()
        if (!isLandscape && (ratioScreen > RATIO_CARD || hImage > heightAvailable)) {
            // screen wider than magic card, we need to calculate from the size
            hImage = heightAvailable;
            wImage = (heightAvailable / RATIO_CARD).toInt();
        }
        if (resources.getBoolean(R.bool.isTablet)) {
            wImage = (wImage * 0.8).toInt()
            hImage = (hImage * 0.8).toInt()
        }
        var par = cardImage.layoutParams as RelativeLayout.LayoutParams;
        par.width = wImage;
        par.height = hImage;
        cardImage.layoutParams = par;
    }

    @OnClick(R.id.image_card_retry)
    fun retryImage(view: View) {
        loadImage(false)
    }

    @OnClick(R.id.price_container)
    fun openPrice(view: View) {
        if (price != null && !price!!.isAnError) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(price?.link))
            context.startActivity(browserIntent)
        }
    }

    fun toggleImage(showImage: Boolean) {
        if (showImage && card?.image != null) {
            loadImage(false)
        } else {
            cardLoader.visibility = View.GONE
            cardImageContainer.visibility = View.GONE
        }
    }

}