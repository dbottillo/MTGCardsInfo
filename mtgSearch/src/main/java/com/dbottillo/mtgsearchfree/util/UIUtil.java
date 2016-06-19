package com.dbottillo.mtgsearchfree.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.view.views.MTGCardView;

public final class UIUtil {

    private UIUtil() {

    }

    public static int dpToPx(Context context, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    public static void setHeight(View view, int height) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp.height != height) {
            lp.height = height;
            view.setLayoutParams(lp);
        }
    }

    public static void setMarginTop(View view, int value) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        if (lp.topMargin != value) {
            lp.topMargin = value;
            view.setLayoutParams(lp);
        }
    }

    public static void setMarginTopLeftRight(View view, int top, int left, int right) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.topMargin = top;
        lp.leftMargin = left;
        lp.rightMargin = right;
        view.setLayoutParams(lp);
    }

    public static void setWidth(View view, int width) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp.width != width) {
            lp.width = width;
            view.setLayoutParams(lp);
        }
    }

    public static void calculateSizeCardImage(ImageView cardImage, int widthAvailable, boolean isTablet){
        int wImage = widthAvailable;
        int hImage = (int) (widthAvailable * MTGCardView.RATIO_CARD);
        if (isTablet) {
            wImage = (int) (wImage * 0.8);
            hImage = (int) (hImage * 0.8);
        }
        RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams) cardImage.getLayoutParams();
        par.width = wImage;
        par.height = hImage;
        LOG.e(wImage+","+hImage);
        cardImage.setLayoutParams(par);
    }

    public static void setMargin(View view, int value) {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        lp.topMargin = value;
        lp.leftMargin = value;
        lp.rightMargin = value;
        lp.bottomMargin = value;
        view.setLayoutParams(lp);
    }
}
