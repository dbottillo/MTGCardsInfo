package com.dbottillo.mtgsearchfree.ui.views;

import android.content.Context;
import android.util.AttributeSet;

public class MTGCardImageView extends android.support.v7.widget.AppCompatImageView {

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = (int) (getMeasuredWidth() * MTGCardView.RATIO_CARD);
        setMeasuredDimension(getMeasuredWidth(), height);
    }

}
