package com.dbottillo.mtgsearchfree.ui.views

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v4.view.animation.FastOutLinearInInterpolator
import android.util.AttributeSet
import android.view.View
import com.dbottillo.mtgsearchfree.R
import java.lang.ref.WeakReference

class MTGLoader : View {

    private var size: Int = 0
    private var maxRadius: Float = 0.toFloat()
    private var currentStep = 0.0f
    private var currentPaintColor = 0
    private var growing = true
    private var argbEvaluator = ArgbEvaluator()
    private var interpolator = FastOutLinearInInterpolator()
    private val paint = Paint()

    private val currentColor: Int
        get() = getColor(currentPaintColor)

    private val nextColor: Int
        get() {
            var next = currentPaintColor + 1
            if (next > 4) {
                next = 0
            }
            return getColor(next)
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paint.isAntiAlias = true
        setPaintColor()
        val loadingHandler = MTGLoaderHandler(this)
        loadingHandler.sendEmptyMessage(0)
    }

    private fun setPaintColor() {
        paint.color = currentColor
    }

    private fun getColor(value: Int): Int {
        return ContextCompat.getColor(context, when (value) {
            0 -> R.color.mtg_white
            1 -> R.color.mtg_blue
            2 -> R.color.mtg_black
            3 -> R.color.mtg_red
            4 -> R.color.mtg_green
            else -> -1
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var value = interpolator.getInterpolation(currentStep)

        if (!growing) {
            val color = argbEvaluator.evaluate(value, currentColor, nextColor) as Int
            paint.color = color
            value = 1.0f - value
        }

        val radius = (maxRadius * value).toInt()
        canvas.drawCircle(size / 2f, size / 2f, radius.toFloat(), paint)

        currentStep += STEP
        if (currentStep >= 1.0f) {
            if (growing) {
                growing = false
            } else {
                currentPaintColor++
                if (currentPaintColor > 4) {
                    currentPaintColor = 0
                }
                growing = true
            }
            currentStep = 0f
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec) // always want a square canvas
        size = measuredWidth
        maxRadius = size / 6f
    }

    private class MTGLoaderHandler internal constructor(loader: MTGLoader) : Handler() {

        private var loader: WeakReference<MTGLoader> = WeakReference(loader)

        @Synchronized override fun handleMessage(msg: Message) {
            val mtgLoader = loader.get()
            if (mtgLoader != null) {
                mtgLoader.invalidate()
                sendEmptyMessageDelayed(0, DELAY_MILLIS.toLong())
            }
        }
    }

    companion object {
        private const val STEP = 0.05f
        private const val DELAY_MILLIS = 40
    }
}