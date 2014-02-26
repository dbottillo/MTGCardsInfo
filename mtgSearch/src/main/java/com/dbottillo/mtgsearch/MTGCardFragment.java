package com.dbottillo.mtgsearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbottillo.adapters.MTGCardListAdapter;
import com.dbottillo.resources.MTGCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGCardFragment extends DBFragment{

    public static final String CARD = "card";

    private MTGCard card;

    private int position;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.card, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card, container, false);

        card = getArguments().getParcelable(CARD);

        ImageView cardImage = (ImageView) rootView.findViewById(R.id.image_card);
        String image = "http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+card.getMultiVerseId()+"&type=card";
        Picasso.with(getActivity()).load(image).placeholder(R.drawable.card_placeholder).into(cardImage);

        TextView cardName = (TextView) rootView.findViewById(R.id.detail_card);
        cardName.setText(card.getType()+"\n"+card.getPower()+"/"+card.getToughness()+", "+card.getManaCost()+" ("+card.getCmc()+")");

        TextView cardText = (TextView) rootView.findViewById(R.id.text_card);
        cardText.setText(card.getText());

        setHasOptionsMenu(true);

        return rootView;
    }
}
