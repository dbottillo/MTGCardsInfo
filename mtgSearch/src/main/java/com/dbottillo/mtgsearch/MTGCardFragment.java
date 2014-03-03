package com.dbottillo.mtgsearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dbottillo.adapters.MTGCardListAdapter;
import com.dbottillo.resources.MTGCard;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGCardFragment extends DBFragment{

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

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        refreshUI();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void refreshUI(){
        TextView cardName = (TextView) getView().findViewById(R.id.detail_card);
        cardName.setText(card.getType()+"\n"+card.getPower()+"/"+card.getToughness()+", "+card.getManaCost()+" ("+card.getCmc()+")");

        TextView cardText = (TextView) getView().findViewById(R.id.text_card);
        cardText.setText(card.getText());

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
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, card.getName());
                String image = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+card.getMultiVerseId()+"&type=card";
                i.putExtra(Intent.EXTRA_TEXT, image);
                startActivity(Intent.createChooser(i, getString(R.id.share_card)));
                return true;
            default:
                break;
        }

        return false;
    }
}
