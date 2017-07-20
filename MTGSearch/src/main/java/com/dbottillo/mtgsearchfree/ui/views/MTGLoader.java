package com.dbottillo.mtgsearchfree.ui.views;


import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.View;

import com.dbottillo.mtgsearchfree.R;

import java.lang.ref.WeakReference;

public class MTGLoader extends View {

    private int size;
    private float maxRadius;
    private float currentStep = 0.0f;
    private int currentPaintColor = 0;
    private boolean growing = true;
    private final static float STEP = 0.05f;
    private final static int DELAY_MILLIS = 40;
    private ArgbEvaluator argbEvaluator;
    private FastOutLinearInInterpolator interpolator;
    private Paint paint = new Paint();

    public MTGLoader(Context context) {
        super(context);
        init();
    }

    public MTGLoader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MTGLoader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        argbEvaluator = new ArgbEvaluator();
        interpolator = new FastOutLinearInInterpolator();
        setPaintColor();
        MTGLoaderHandler loadingHandler = new MTGLoaderHandler(this);
        loadingHandler.sendEmptyMessage(0);
    }

    private void setPaintColor() {
        paint.setColor(getCurrentColor());
    }

    private int getCurrentColor() {
        return getColor(currentPaintColor);
    }

    private int getNextColor() {
        int next = currentPaintColor + 1;
        if (next > 4) {
            next = 0;
        }
        return getColor(next);
    }

    private int getColor(int value) {
        Resources resources = getResources();
        if (value == 0) {
            return resources.getColor(R.color.mtg_white);
        }
        if (value == 1) {
            return resources.getColor(R.color.mtg_blue);
        }
        if (value == 2) {
            return resources.getColor(R.color.mtg_black);
        }
        if (value == 3) {
            return resources.getColor(R.color.mtg_red);
        }
        if (value == 4) {
            return resources.getColor(R.color.mtg_green);
        }
        return -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float value = interpolator.getInterpolation(currentStep);

        if (!growing) {
            int color = (Integer) argbEvaluator.evaluate(value, getCurrentColor(), getNextColor());
            paint.setColor(color);
            value = 1.0f - value;
        }

        int radius = (int) (maxRadius * value);
        canvas.drawCircle(size / 2.f,  size / 2.f, radius, paint);

        currentStep += STEP;
        if (currentStep >= 1.0f) {
            if (growing) {
                growing = false;
            } else {
                currentPaintColor++;
                if (currentPaintColor > 4) {
                    currentPaintColor = 0;
                }
                growing = true;
            }
            currentStep = 0;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // always want a square canvas

        size = getMeasuredWidth();
        maxRadius = size / 6.f;
    }

    private static class MTGLoaderHandler extends Handler {

        WeakReference<MTGLoader> loader;

        MTGLoaderHandler(MTGLoader loader) {
            this.loader = new WeakReference<>(loader);
        }

        @Override
        public synchronized void handleMessage(Message msg) {
            MTGLoader mtgLoader = loader.get();
            if (mtgLoader != null) {
                mtgLoader.invalidate();
                sendEmptyMessageDelayed(0, DELAY_MILLIS);
            }
        }
    }

}