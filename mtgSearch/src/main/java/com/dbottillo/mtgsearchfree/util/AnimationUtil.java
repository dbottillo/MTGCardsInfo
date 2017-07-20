package com.dbottillo.mtgsearchfree.util;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;

public final class AnimationUtil {

    private AnimationUtil() {

    }

    private static final int DEFAULT_DURATION = 200;

    public static ValueAnimator animateHeight(final View view, int target) {
        if (view.getHeight() == target) {
            return null;
        }
        ValueAnimator anim = ValueAnimator.ofInt(view.getHeight(), target);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                UIUtil.setHeight(view, val);
            }
        });
        anim.setDuration(DEFAULT_DURATION);
        anim.start();
        return anim;
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

    public static LinearInterpolator createLinearInterpolator() {
        return new LinearInterpolator();
    }

    public static class LinearInterpolator implements TimeInterpolator {

        private float mStartValue;
        private float mEndValue;

        LinearInterpolator() {
        }

        public final LinearInterpolator fromValue(final float startValue) {
            mStartValue = startValue;
            return this;
        }

        public final LinearInterpolator toValue(final float endValue) {
            mEndValue = endValue;
            return this;
        }

        @Override
        public final float getInterpolation(final float input) {
            final float amount = Math.abs(mEndValue - mStartValue);
            if (mEndValue > mStartValue) {
                return mStartValue + (amount * input);
            } else {
                return mStartValue - (amount * input);
            }
        }
    }

    public static ArgbInterpolator createArgbInterpolator() {
        return new ArgbInterpolator();
    }

    public static class ArgbInterpolator {

        private int mStartValue;
        private int mEndValue;
        private ArgbEvaluator argbEvaluator;

        ArgbInterpolator() {
            argbEvaluator = new ArgbEvaluator();
        }

        public final ArgbInterpolator fromValue(final int startValue) {
            mStartValue = startValue;
            return this;
        }

        public final ArgbInterpolator toValue(final int endValue) {
            mEndValue = endValue;
            return this;
        }

        public final int getInterpolation(final float input) {
            return (int) argbEvaluator.evaluate(input, mStartValue, mEndValue);
        }
    }

}
