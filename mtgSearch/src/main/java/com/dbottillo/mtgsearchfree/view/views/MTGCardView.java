package com.dbottillo.mtgsearchfree.view.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.model.TCGPrice;
import com.dbottillo.mtgsearchfree.model.network.NetworkIntentService;
import com.dbottillo.mtgsearchfree.util.LOG;
import com.dbottillo.mtgsearchfree.util.TrackingManager;
import com.dbottillo.mtgsearchfree.util.UIUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MTGCardView extends RelativeLayout {

    @Bind(R.id.fragment_card_container)
    View mainContainer;
    @Bind(R.id.detail_card)
    TextView detailCard;
    @Bind(R.id.price_on_tcg)
    TextView priceOnTcg;
    @Bind(R.id.price_card)
    TextView cardPrice;
    @Bind(R.id.image_card_retry)
    View retry;
    @Bind(R.id.image_card)
    ImageView cardImage;
    @Bind(R.id.image_card_loader)
    MTGLoader cardLoader;
    @Bind(R.id.image_card_container)
    View cardImageContainer;

    public static float RATIO_CARD = 1.39622641509434f;
    boolean isLandscape = false;

    public MTGCard getCard() {
        return card;
    }

    MTGCard card;
    TCGPrice price;

    public MTGCardView(Context ctx) {
        this(ctx, null);
    }

    public MTGCardView(Context ctx, AttributeSet attrs) {
        this(ctx, attrs, -1);
    }

    public MTGCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_mtg_card, this, true);
        ButterKnife.bind(this, view);

        priceOnTcg.setText(Html.fromHtml("<i><u>TCG</i></u>"));

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int paddingCard = getResources().getDimensionPixelSize(R.dimen.padding_card_image);
        int widthAvailable = size.x - paddingCard * 2;
        if (isLandscape) {
            widthAvailable = size.x / 2 - paddingCard * 2;
        }
        UIUtil.calculateSizeCardImage(cardImage, widthAvailable, getResources().getBoolean(R.bool.isTablet));
    }

    private BroadcastReceiver priceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            price = intent.getParcelableExtra(NetworkIntentService.REST_RESULT);
            updatePrice();
            if (price != null && price.isNotFound() && isNetworkAvailable()) {
                String url = intent.getStringExtra(NetworkIntentService.REST_URL);
                TrackingManager.trackPriceError(url);
            }
        }
    };

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LOG.d();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(priceReceiver);
    }

    public void load(MTGCard card, boolean showImage) {
        LOG.d();
        this.card = card;
        String manaCost;
        String rulings;
        if (card.getManaCost() != null) {
            manaCost = card.getManaCost() + " (" + card.getCmc() + ")";
        } else {
            manaCost = " - ";
        }
        if (card.getRulings().size() > 0) {
            StringBuilder html = new StringBuilder();
            for (String rule : card.getRulings()) {
                html.append("- ").append(rule).append("<br/><br/>");
            }
            rulings = html.toString();
        } else {
            rulings = "";
        }
        detailCard.setText(Html.fromHtml(getContext().getResources().getString(R.string.card_detail, card.getType(),
                card.getPower(), card.getToughness(), manaCost, card.getText(), card.getSet().getName(), rulings)));

        Intent intent = new Intent(getContext(), NetworkIntentService.class);
        Bundle params = new Bundle();
        params.putString(NetworkIntentService.EXTRA_ID, card.getMultiVerseId() + "");
        params.putString(NetworkIntentService.EXTRA_CARD_NAME, card.getName());
        params.putString(NetworkIntentService.EXTRA_SET_NAME, card.getSet().getName());
        intent.putExtra(NetworkIntentService.EXTRA_PARAMS, params);
        getContext().startService(intent);

        retry.setVisibility(View.GONE);

        if (showImage && card.getImage() != null) {
            loadImage(false);
        } else {
            cardLoader.setVisibility(View.GONE);
            cardImageContainer.setVisibility(View.GONE);
        }

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(priceReceiver);
        IntentFilter cardFilter = new IntentFilter(card.getMultiVerseId() + "");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(priceReceiver, cardFilter);
    }

    private void updatePrice() {
        LOG.d();
        if (price == null) {
            return;
        }
        if (price.isAnError()) {
            cardPrice.setText(price.getErrorPrice());
        } else {
            cardPrice.setText(price.toDisplay(isLandscape));
        }
    }

    private void loadImage(final boolean fallback) {
        LOG.d();
        retry.setVisibility(View.GONE);
        cardImageContainer.setVisibility(View.VISIBLE);
        cardImage.setVisibility(View.GONE);
        startCardLoader();
        Picasso.with(getContext().getApplicationContext()).load(fallback ? card.getImageFromGatherer() : card.getImage())
                .into(cardImage, new Callback() {
                    public void onSuccess() {
                        cardImage.setVisibility(View.VISIBLE);
                        stopCardLoader();
                    }

                    public void onError() {
                        if (!fallback) {
                            // need to try to loadSet from gatherer
                            loadImage(true);
                            return;
                        }
                        stopCardLoader();
                        cardImage.setVisibility(View.GONE);
                        retry.setVisibility(View.VISIBLE);
                        if (isNetworkAvailable()) {
                            TrackingManager.trackImageError(card.getImage());
                        }
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void startCardLoader() {
        cardLoader.setVisibility(View.VISIBLE);
    }

    private void stopCardLoader() {
        cardLoader.setVisibility(View.GONE);
    }

    @OnClick(R.id.image_card_retry)
    public void retryImage(View view) {
        loadImage(false);
    }

    @OnClick(R.id.price_container)
    public void openPrice(View view) {
        LOG.d();
        if (price != null && !price.isAnError()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(price.getLink()));
            getContext().startActivity(browserIntent);
        }
    }

    public void toggleImage(boolean showImage) {
        LOG.d();
        if (showImage && card.getImage() != null) {
            loadImage(false);
        } else {
            cardLoader.setVisibility(View.GONE);
            cardImageContainer.setVisibility(View.GONE);
        }
    }

}
