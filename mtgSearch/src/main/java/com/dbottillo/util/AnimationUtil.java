package com.dbottillo.util;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;

import com.dbottillo.view.SlidingUpPanelLayout;

public final class AnimationUtil {

    private AnimationUtil(){

    }

    private static final int DEFALT_DURATION = 200;

    public static void animteSlidingPanelHeight(final SlidingUpPanelLayout slidingPaneLayout, int target) {
        if (slidingPaneLayout.getPanelHeight() == target) {
            return;
        }
        ValueAnimator anim = ValueAnimator.ofInt(slidingPaneLayout.getPanelHeight(), target);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                slidingPaneLayout.setPanelHeight(val);
            }
        });
        anim.setDuration(DEFALT_DURATION);
        anim.start();
    }

    public static void growView(View view) {
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f));
        scaleUp.setDuration(250);
        scaleUp.start();
    }

    public static void shrinkView(View view) {
        ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f));
        scaleUp.setDuration(250);
        scaleUp.start();
    }



}
