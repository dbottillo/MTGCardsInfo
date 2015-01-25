package com.dbottillo.cards;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.base.DBFragment;
import com.dbottillo.helper.TrackingHelper;
import com.dbottillo.network.NetworkIntentService;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.TCGPrice;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MTGCardFragment extends DBFragment {

    private static final String TAG = MTGCardFragment.class.getName();

    public interface CardConnector {
        boolean isCardSaved(MTGCard card);

        void saveCard(MTGCard card);

        void removeCard(MTGCard card);

        void tapOnImage(int position);
    }

    private static final float RATIO_CARD = 1.39622641509434f;

    private int widthAvailable;
    private int heightAvailable;

    public static final String CARD = "card";
    public static final String FULLSCREEN = "fullscreen";
    public static final String POSITION = "position";

    View mainContainer;

    boolean isLandscape;
    boolean fullscreenMode = false;
    private MTGCard card;
    ImageView cardImage;
    ImageView cardLoader;
    View retry;
    Button retryBtn;
    View cardImageContainer;
    TextView priceCard;
    TCGPrice price;

    private int position;

    private CardConnector cardConnector;

    public static MTGCardFragment newInstance(MTGCard card, int position, boolean fullscreen) {
        MTGCardFragment fragment = new MTGCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(CARD, card);
        args.putBoolean(FULLSCREEN, fullscreen);
        args.putInt(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public MTGCardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_card, container, false);

        card = getArguments().getParcelable(CARD);
        position = getArguments().getInt(POSITION);
        fullscreenMode = getArguments().getBoolean(FULLSCREEN);
        mainContainer = rootView.findViewById(R.id.fragment_card_container);
        cardImage = (ImageView) rootView.findViewById(R.id.image_card);
        cardImageContainer = rootView.findViewById(R.id.image_card_container);
        cardLoader = (ImageView) rootView.findViewById(R.id.image_card_loader);
        retry = rootView.findViewById(R.id.image_card_retry);
        retryBtn = (Button) rootView.findViewById(R.id.image_card_retry_btn);
        priceCard = (TextView) rootView.findViewById(R.id.price_card);

        setHasOptionsMenu(true);

        TextView priceTcg = (TextView) rootView.findViewById(R.id.price_on_tcg);
        priceTcg.setText(Html.fromHtml("<i><u>TCG</i></u>"));

        mainContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isAttached) {
                    int paddingCard = getResources().getDimensionPixelSize(R.dimen.padding_card_image);
                    if (fullscreenMode) {
                        paddingCard = 0;
                    }
                    widthAvailable = mainContainer.getWidth() - paddingCard * 2;
                    if (isLandscape) {
                        widthAvailable = (mainContainer.getWidth() / 2) - paddingCard * 2;
                    }
                    heightAvailable = mainContainer.getHeight() - paddingCard;
/*                    if (!isLandscape) {
                        heightAvailable -= getResources().getDimensionPixelSize(R.dimen.price_height_container);
                    }*/
                    updateSizeImage();
                    if (Build.VERSION.SDK_INT < 16) {
                        mainContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mainContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });

        rootView.findViewById(R.id.price_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (price != null && !price.isAnError()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(price.getLink()));
                    startActivity(browserIntent);
                }
            }
        });

        //cardImageContainer.setBackgroundColor(((MTGCard) card).getMtgColor(getActivity()))
        //retryBtn.setBackgroundColor(((MTGCard) card).getMtgColor(getActivity()));
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });

        cardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardConnector.tapOnImage(position);
            }
        });

        return rootView;
    }

    private boolean isAttached = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        isAttached = true;

        try {
            cardConnector = (CardConnector) activity;
            isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        } catch (ClassCastException e) {
            Log.e(TAG, "activity must implement databaseconnector interface");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("price", price);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter cardFilter = new IntentFilter(((MTGCard) card).getMultiVerseId() + "");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(priceReceiver, cardFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(priceReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            price = savedInstanceState.getParcelable("price");
        }
        refreshUI();
    }


    private void refreshUI() {
        TextView cardName = (TextView) getView().findViewById(R.id.detail_card);

        String typeHtml = "<b>" + getString(R.string.type_card) + ":</b> " + card.getType() + " <br/><br/> " +
                "<b>" + getString(R.string.pt_card) + "</b>: " + card.getPower() + "/" + card.getToughness() + " <br/><br/>";
        typeHtml += "<b>" + getString(R.string.manacost_card) + "</b>: ";
        if (card.getManaCost() != null) {
            typeHtml += card.getManaCost() + " (" + card.getCmc() + ")";
        } else {
            typeHtml += " - ";
        }
        cardName.setText(Html.fromHtml(typeHtml));

        TextView cardText = (TextView) getView().findViewById(R.id.text_card);
        cardText.setText(card.getText());

        TextView setCardText = (TextView) getView().findViewById(R.id.set_card);
        setCardText.setText(Html.fromHtml("<b>" + getString(R.string.set_card) + ":</b> " + card.getSetName()));

        if (price == null) {
            priceCard.setText(R.string.loading);
        } else {
            updatePrice();
        }

        Intent intent = new Intent(getActivity(), NetworkIntentService.class);
        Bundle params = new Bundle();
        params.putString(NetworkIntentService.EXTRA_ID, card.getMultiVerseId() + "");
        params.putString(NetworkIntentService.EXTRA_CARD_NAME, card.getName().replace(" ", "%20"));
        intent.putExtra(NetworkIntentService.EXTRA_PARAMS, params);
        getActivity().startService(intent);

        retry.setVisibility(View.GONE);

        if (getSharedPreferences().getBoolean(PREF_SHOW_IMAGE, true) && card.getImage() != null) {
            loadImage();
        } else {
            cardLoader.setVisibility(View.GONE);
            cardImageContainer.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver priceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            price = intent.getParcelableExtra(NetworkIntentService.REST_RESULT);
            updatePrice();
            if (price.isAnError()) {
                String url = intent.getStringExtra(NetworkIntentService.REST_URL);
                TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "price", url);
            }
        }
    };

    private void updatePrice() {
        if (price.isAnError()) {
            priceCard.setText(price.getErrorPrice());
        } else {
            priceCard.setText(price.toDisplay(isLandscape));
        }
    }

    private void updateSizeImage() {
        float ratioScreen = (float) heightAvailable / (float) widthAvailable;
        // screen taller than magic card, we can take the width
        int wImage = widthAvailable;
        int hImage = (int) (widthAvailable * RATIO_CARD);
        if (!isLandscape && (ratioScreen > RATIO_CARD || hImage > heightAvailable)) {
            // screen wider than magic card, we need to calculate from the size
            hImage = heightAvailable;
            wImage = (int) (heightAvailable / RATIO_CARD);
        }
        if (getResources().getBoolean(R.bool.isTablet)) {
            wImage = (int) (wImage * 0.8);
            hImage = (int) (hImage * 0.8);
        }
        RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams) cardImage.getLayoutParams();
        par.width = wImage;
        par.height = hImage;
        if (fullscreenMode) {
            par.topMargin = 0;
            par.leftMargin = 0;
            par.rightMargin = 0;
            par.bottomMargin = 0;
        }
        cardImage.setLayoutParams(par);
    }

    private void loadImage() {
        retry.setVisibility(View.GONE);
        cardImageContainer.setVisibility(View.VISIBLE);

        cardImage.setVisibility(View.GONE);

        startCardLoader();
        Picasso.with(getActivity()).load(card.getImage()).into(cardImage, new Callback() {

            @Override
            public void onSuccess() {
                cardImage.setVisibility(View.VISIBLE);
                stopCardLoader();
            }

            @Override
            public void onError() {
                stopCardLoader();
                cardImage.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
                TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "image", card.getImage());
                if (isNetworkAvailable()) {
                    TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_ERROR, "image-with-connection", card.getImage());
                }
            }
        });
    }

    private boolean isNetworkAvailable() {
        if (getActivity() == null) return false;
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void startCardLoader() {
        cardLoader.setVisibility(View.VISIBLE);
        final AnimationDrawable frameAnimation = (AnimationDrawable) cardLoader.getBackground();
        cardLoader.post(new Runnable() {
            public void run() {
                frameAnimation.start();
            }
        });
    }

    private void stopCardLoader() {
        final AnimationDrawable frameAnimation = (AnimationDrawable) cardLoader.getBackground();
        cardLoader.post(new Runnable() {
            public void run() {
                frameAnimation.stop();
                cardLoader.setVisibility(View.GONE);
            }
        });
    }

    private boolean isSavedOffline = false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.card, menu);

        MenuItem item = menu.findItem(R.id.action_fav);
        if (cardConnector.isCardSaved(card)) {
            item.setTitle(getString(R.string.favourite_remove));
            item.setIcon(R.drawable.ab_star_colored);
            isSavedOffline = true;
        } else {
            item.setTitle(getString(R.string.favourite_add));
            item.setIcon(R.drawable.ab_star);
            isSavedOffline = false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_share) {
            TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_CARD, TrackingHelper.UA_ACTION_SHARE, card.getName());
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, card.getName());
            String url = "http://gatherer.wizards.com/Pages/Card/Details.aspx?multiverseid=" + ((MTGCard) card).getMultiVerseId();
            i.putExtra(Intent.EXTRA_TEXT, url);
            startActivity(Intent.createChooser(i, getString(R.id.share_card)));
            return true;
        } else if (i1 == R.id.action_fav) {
            if (isSavedOffline) {
                cardConnector.removeCard(card);
                TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_FAVOURITE, TrackingHelper.UA_ACTION_SAVED, card.getId() + "");
            } else {
                cardConnector.saveCard(card);
                TrackingHelper.trackEvent(TrackingHelper.UA_CATEGORY_FAVOURITE, TrackingHelper.UA_ACTION_UNSAVED, card.getId() + "");
            }
            getActivity().invalidateOptionsMenu();
        }

        return false;
    }

    @Override
    public String getPageTrack() {
        return "/card/" + ((MTGCard) card).getMultiVerseId();
    }
}
