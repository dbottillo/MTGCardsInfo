package com.dbottillo.mtgsearchfree.view.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.mtgsearchfree.model.CardsCollection;
import com.dbottillo.mtgsearchfree.model.MTGCard;
import com.dbottillo.mtgsearchfree.view.views.MTGCardView;

import java.util.List;

public class CardsPagerAdapter extends PagerAdapter {

    private Context context;
    private boolean showImage;
    private CardsCollection cards;

    public CardsPagerAdapter(Context context, boolean showImage, CardsCollection cards) {
        this.context = context;
        this.showImage = showImage;
        this.cards = cards;
    }

    public int getCount() {
        return cards.getList().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        MTGCardView view = new MTGCardView(context);
        view.load(cards.getList().get(position), showImage);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    public CharSequence getPageTitle(int position) {
        MTGCard card = cards.getList().get(position);
        if (cards.isDeck()) {
            return card.getName() + " (" + card.getQuantity() + ")";
        }
        return card.getName();
    }

    public MTGCard getItem(int currentItem) {
        return cards.getList().get(currentItem);
    }

}
