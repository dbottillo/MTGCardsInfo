package com.dbottillo.util;

import android.animation.ValueAnimator;

import com.dbottillo.view.SlidingUpPanelLayout;

public class AnimationUtil {

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
}
