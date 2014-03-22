package com.dbottillo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class AdaptImageView extends ImageView {

    public AdaptImageView(Context context) {
        super(context);
    }

    public AdaptImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * 1.39);
        setMeasuredDimension(width, height);
    }
}
