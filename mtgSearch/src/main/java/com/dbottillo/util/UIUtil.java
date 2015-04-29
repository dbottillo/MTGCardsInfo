package com.dbottillo.util;

import android.view.View;
import android.view.ViewGroup;

public class UIUtil {

    public static void setHeight(View view, int height) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp.height != height) {
            lp.height = height;
            view.setLayoutParams(lp);
        }
    }
}
