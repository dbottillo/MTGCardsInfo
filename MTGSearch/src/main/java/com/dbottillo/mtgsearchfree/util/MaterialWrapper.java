package com.dbottillo.mtgsearchfree.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

public final class MaterialWrapper {

    private MaterialWrapper() {

    }

    public static void setElevation(View view, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(value);
        }
    }

    public static void copyElevation(View targetView, View copiedView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            targetView.setElevation(copiedView.getElevation());
        }
    }

    public static Drawable getRippleDrawable(Context context, String drawableName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawableName = "ripple_" + drawableName;
        }
        int id = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
        return context.getResources().getDrawable(id);
    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        }
    }

    public static void setNavigationBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setNavigationBarColor(color);
        }
    }

    public static void setTint(@NonNull Drawable view, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setTint(color);
        } else {
            view.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }

    public static void setLightStatusBar(@NonNull Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static void setDarkStatusBar(@NonNull Window window) {
        window.getDecorView().setSystemUiVisibility(0);
    }

}
