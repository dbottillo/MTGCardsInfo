package com.dbottillo.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbottillo.BuildConfig;
import com.dbottillo.R;
import com.dbottillo.base.DBActivity;
import com.dbottillo.base.DBFragment;
import com.dbottillo.base.MTGApp;
import com.dbottillo.network.NetworkIntentService;
import com.dbottillo.resources.GameCard;
import com.dbottillo.resources.HSCard;
import com.dbottillo.resources.MTGCard;
import com.google.android.gms.ads.AdListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGCardFragment extends DBFragment {

    private static final String TAG = MTGCardFragment.class.getName();

    public interface DatabaseConnector {
        boolean isCardSaved(GameCard card);

        void saveCard(GameCard card);

        void removeCard(GameCard card);
    }

    private static final float RATIO_CARD = 1.39622641509434f;

    private int widthAvailable;
    private int heightAvailable;

    public static final String CARD = "card";

    View mainContainer;

    boolean isLandscape;

    private GameCard card;
    ImageView cardImage;
    ImageView cardLoader;
    View retry;
    View cardImageContainer;
    TextView priceCard;

    String urlImage = null;

    private DatabaseConnector databaseConnector;

    public static MTGCardFragment newInstance(GameCard card) {
        MTGCardFragment fragment = new MTGCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(CARD, card);
        fragment.setArguments(args);
        return fragment;
    }

    public MTGCardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_card, container, false);

        card = getArguments().getParcelable(CARD);
        mainContainer = rootView.findViewById(R.id.fragment_card_container);
        cardImage = (ImageView) rootView.findViewById(R.id.image_card);
        cardImageContainer = rootView.findViewById(R.id.image_card_container);
        cardLoader = (ImageView) rootView.findViewById(R.id.image_card_loader);
        retry = rootView.findViewById(R.id.image_card_retry);
        priceCard = (TextView) rootView.findViewById(R.id.price_card);

        rootView.findViewById(R.id.price_card_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DBActivity) getActivity()).openDialog(DBActivity.DBDialog.PRICE_INFO);
            }
        });

        setHasOptionsMenu(true);

        if (!getApp().isPremium()) {
            createAdView("ca-app-pub-8119815713373556/8777882818");
            getAdView().setAdListener(new AdListener() {
                @Override
                public void onAdOpened() {
                    // Save app state before going to the ad overlay.
                }
            });

            FrameLayout layout = (FrameLayout) rootView.findViewById(R.id.banner_container);
            layout.addView(getAdView());

            getAdView().loadAd(createAdRequest());
        }

        mainContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int paddingCard = getResources().getDimensionPixelSize(R.dimen.padding_card_image);
                widthAvailable = mainContainer.getWidth() - paddingCard * 2;
                if (isLandscape) {
                    widthAvailable = (mainContainer.getWidth() / 2) - paddingCard * 2;
                }
                heightAvailable = mainContainer.getHeight() - paddingCard * 2;
                updateSizeImage();
                if (Build.VERSION.SDK_INT < 16) {
                    mainContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mainContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            databaseConnector = (DatabaseConnector) activity;
            isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        } catch (ClassCastException e) {
            Log.e(TAG, "activity must implement databaseconnector interface");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (card instanceof MTGCard) {
            IntentFilter cardFilter = new IntentFilter(((MTGCard) card).getMultiVerseId() + "");
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(priceReceiver, cardFilter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (card instanceof MTGCard) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(priceReceiver);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshUI();
    }


    private void refreshUI() {
        TextView cardName = (TextView) getView().findViewById(R.id.detail_card);

        if (BuildConfig.magic) {
            MTGCard mtgCard = (MTGCard) card;

            String typeHtml = "<b>" + getString(R.string.type_card) + ":</b> " + mtgCard.getType() + " <br/><br/> " +
                    "<b>" + getString(R.string.pt_card) + "</b>: " + mtgCard.getPower() + "/" + mtgCard.getToughness() + " <br/><br/>";
            typeHtml += "<b>" + getString(R.string.manacost_card) + "</b>: " + card.getManaCost() + " (" + mtgCard.getCmc() + ")";
            cardName.setText(Html.fromHtml(typeHtml));

            TextView cardText = (TextView) getView().findViewById(R.id.text_card);
            cardText.setText(mtgCard.getText());

            TextView setCardText = (TextView) getView().findViewById(R.id.set_card);
            setCardText.setText(Html.fromHtml("<b>" + getString(R.string.set_card) + ":</b> " + card.getSetName()));

            if (mtgCard.getMultiVerseId() > 0) {
                urlImage = "http://mtgimage.com/multiverseid/" + mtgCard.getMultiVerseId() + ".jpg";
            }

            updatePriceCard(getString(R.string.loading));

            Intent intent = new Intent(getActivity(), NetworkIntentService.class);
            Bundle params = new Bundle();
            params.putString(NetworkIntentService.EXTRA_ID, mtgCard.getMultiVerseId() + "");
            params.putString(NetworkIntentService.EXTRA_CARD_NAME, card.getName().replace(" ", "%20"));
            intent.putExtra(NetworkIntentService.EXTRA_PARAMS, params);
            getActivity().startService(intent);

        } else {

            HSCard hearthstoneCard = (HSCard) card;

            String typeHtml = "<b>" + getString(R.string.type_card) + ":</b> " + hearthstoneCard.getType() + " <br/><br/> " +
                    "<b>" + getString(R.string.ah_card) + "</b>: " + hearthstoneCard.getAttack() + "/" + hearthstoneCard.getHealth() + " <br/><br/>" +
                    "<b>" + getString(R.string.cost_card) + "</b>: " + card.getManaCost() + " <br/><br/>" +
                    "<b>" + getString(R.string.rarity_card) + "</b>: " + card.getRarity();/*+ " <br/><br/>"+
                    "<b>" + getString(R.string.faction_card) + "</b>: " + ((HSCard) card).getFaction();*/
            cardName.setText(Html.fromHtml(typeHtml));

            TextView cardText = (TextView) getView().findViewById(R.id.text_card);
            cardText.setText(Html.fromHtml(hearthstoneCard.getText()));

            TextView mechanics = (TextView) getView().findViewById(R.id.set_card);
            if (hearthstoneCard.getMechanics().length() != 0) {
                mechanics.setText(Html.fromHtml("<b>" + getString(R.string.mechanics_card) + "</b> " + hearthstoneCard.getMechanics()));
            } else {
                mechanics.setText("");
            }

            if (hearthstoneCard.getHearthstoneId() != null) {
                urlImage = "http://wow.zamimg.com/images/hearthstone/cards/enus/original/" + hearthstoneCard.getHearthstoneId() + ".png";
            }
        }

        retry.setVisibility(View.GONE);

        if (getSharedPreferences().getBoolean(PREF_SHOW_IMAGE, true) && urlImage != null) {
            loadImage();
        } else {
            cardLoader.setVisibility(View.GONE);
            cardImageContainer.setVisibility(View.GONE);
        }

        getView().findViewById(R.id.image_card_retry_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImage();
            }
        });
    }

    private BroadcastReceiver priceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String price = intent.getStringExtra(NetworkIntentService.REST_RESULT);
            updatePriceCard(price);
        }
    };

    private void updatePriceCard(String message) {
        priceCard.setText(Html.fromHtml("<b>Price</b>: " + message));
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
        cardImage.setLayoutParams(par);
    }

    private void loadImage() {
        retry.setVisibility(View.GONE);
        cardImageContainer.setVisibility(View.VISIBLE);

        cardImage.setVisibility(View.GONE);

        startCardLoader();
        Picasso.with(getActivity()).load(urlImage).into(cardImage, new Callback() {

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
            }
        });
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
        if (databaseConnector.isCardSaved(card)) {
            item.setTitle(getString(R.string.favourite_remove));
            item.setIcon(R.drawable.ab_star_colored);
            isSavedOffline = true;
        } else {
            item.setTitle(getString(R.string.favourite_add));
            item.setIcon(R.drawable.ab_star);
            isSavedOffline = false;
        }

        MenuItem share = menu.findItem(R.id.action_share);
        share.setVisible(BuildConfig.magic);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_share) {
            trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_OPEN, "share");
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, card.getName());
            String image = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + ((MTGCard) card).getMultiVerseId() + "&type=card";
            i.putExtra(Intent.EXTRA_TEXT, image);
            startActivity(Intent.createChooser(i, getString(R.id.share_card)));
            return true;
        } else if (i1 == R.id.action_fav) {
            if (isSavedOffline) {
                databaseConnector.removeCard(card);
                trackEvent(MTGApp.UA_CATEGORY_FAVOURITE, MTGApp.UA_ACTION_SAVED, card.getId() + "");
            } else {
                databaseConnector.saveCard(card);
                trackEvent(MTGApp.UA_CATEGORY_FAVOURITE, MTGApp.UA_ACTION_UNSAVED, card.getId() + "");
            }
            getActivity().invalidateOptionsMenu();
        }

        return false;
    }

    @Override
    public String getPageTrack() {
        if (BuildConfig.magic) return "/card/" + ((MTGCard) card).getMultiVerseId();
        return "/card/" + card.getId();
    }
}
