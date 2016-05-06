package com.dbottillo.mtgsearchfree.view.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, (int) (widthMeasureSpec / MTGCardView.RATIO_CARD));
    }

}
