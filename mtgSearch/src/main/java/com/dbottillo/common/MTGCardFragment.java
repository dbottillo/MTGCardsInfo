package com.dbottillo.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.base.DBFragment;
import com.dbottillo.base.MTGApp;
import com.dbottillo.R;
import com.dbottillo.resources.MTGCard;
import com.google.android.gms.ads.AdListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGCardFragment extends DBFragment {

    public static final String CARD = "card";

    private MTGCard card;

    private SmoothProgressBar progressBar;

    public static MTGCardFragment newInstance(MTGCard card) {
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
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);

        card = getArguments().getParcelable(CARD);

        progressBar = (SmoothProgressBar) rootView.findViewById(R.id.progress);

        setHasOptionsMenu(true);

        if (!getApp().isPremium()) {
            createAdView("ca-app-pub-8119815713373556/8777882818");
            getAdView().setAdListener(new AdListener() {
                @Override
                public void onAdOpened() {
                    // Save app state before going to the ad overlay.
                }
            });

            FrameLayout layout = (FrameLayout)rootView.findViewById(R.id.banner_container);
            layout.addView(getAdView());

            getAdView().loadAd(createAdRequest());
        }

        return rootView;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        refreshUI();
    }


    private void refreshUI(){
        TextView cardName = (TextView) getView().findViewById(R.id.detail_card);
        String typeHtml = "<b>Type:</b> "+card.getType()+" <br/><br/> <b>P/T</b>: "+card.getPower()+"/"+card.getToughness()+" <br/><br/>";
        typeHtml += "<b>Mana Cost</b>: "+card.getManaCost()+" ("+card.getCmc()+")";
        cardName.setText(Html.fromHtml(typeHtml));

        TextView cardText = (TextView) getView().findViewById(R.id.text_card);
        cardText.setText(card.getText());

        TextView setCardText = (TextView) getView().findViewById(R.id.set_card);
        setCardText.setText(Html.fromHtml("<b>Set:</b> "+card.getSetName()));

        final View cardImageContainer = getView().findViewById(R.id.image_card_container);
        ImageView cardImage = (ImageView) getView().findViewById(R.id.image_card);
        if (getSharedPreferences().getBoolean(PREF_SHOW_IMAGE, true) && card.getMultiVerseId() > 0){
            cardImageContainer.setVisibility(View.VISIBLE);
            final String image = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+card.getMultiVerseId()+"&type=card";
            progressBar.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(image).placeholder(R.drawable.card_placeholder).into(cardImage,new Callback() {

                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    progressBar.setVisibility(View.GONE);
                    cardImageContainer.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), getString(R.string.error_image), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            progressBar.setVisibility(View.GONE);
            cardImageContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i1 = item.getItemId();
        if (i1 == R.id.action_share) {
            trackEvent(MTGApp.UA_CATEGORY_UI, MTGApp.UA_ACTION_OPEN, "share");
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, card.getName());
            String image = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid=" + card.getMultiVerseId() + "&type=card";
            i.putExtra(Intent.EXTRA_TEXT, image);
            startActivity(Intent.createChooser(i, getString(R.id.share_card)));
            return true;
        } else {
        }

        return false;
    }

    @Override
    public String getPageTrack() {
        return "/card/"+card.getMultiVerseId();
    }
}
