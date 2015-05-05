package com.dbottillo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dbottillo.cards.MTGCardFragment;

public class MTGCardImageView extends ImageView {
    public MTGCardImageView(Context context) {
        super(context);
    }

    public MTGCardImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MTGCardImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension((int) (height / MTGCardFragment.RATIO_CARD), height);
    }
}
