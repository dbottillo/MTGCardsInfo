package com.dbottillo.mtgsearchfree.decks

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.dbottillo.mtgsearchfree.model.Color

@Suppress("MagicNumber")
class DeckIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val rect: RectF = RectF(0f, 0f, 0f, 0f)

    var colors: List<Color> = emptyList()
        set(value) {
            if (field != value || arcs.isEmpty()) {
                field = value
                computeArcs()
            }
        }

    private val paint: Paint = Paint()
    private val greyColor = ContextCompat.getColor(context, R.color.mtg_other)
    private var sweepAngle = 0
    private var arcs = emptyList<Arc>()
    private var animator: ValueAnimator? = null

    private val colorMap = mapOf(Color.WHITE to ContextCompat.getColor(context, R.color.mtg_white),
        Color.BLUE to ContextCompat.getColor(context, R.color.mtg_blue),
        Color.BLACK to ContextCompat.getColor(context, R.color.mtg_black),
        Color.RED to ContextCompat.getColor(context, R.color.mtg_red),
        Color.GREEN to ContextCompat.getColor(context, R.color.mtg_green))

    private fun computeArcs() {
        arcs = if (colors.isEmpty()) {
            listOf(Arc(0f, 360f, 360f, greyColor))
        } else {
            val sweepSize: Float = if (colors.isEmpty()) 360f else 360f / colors.size
            colors.mapIndexed { index, color ->
                val startAngle = index * sweepSize
                Arc(start = startAngle, end = startAngle + sweepSize,
                    sweep = sweepSize, color = colorMap.getValue(color))
            }
        }
        start()
    }

    init {
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    private fun start() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            interpolator = LinearInterpolator()
            addUpdateListener {
                drawProgress(it.animatedValue as Float)
            }
        }
        animator?.start()
    }

    private fun drawProgress(value: Float) {
        sweepAngle = (value * 360f).toInt()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        arcs.forEach { arc ->
            if (sweepAngle > arc.end) {
                paint.color = arc.color
                canvas.drawArc(rect, START_ANGLE + arc.start, arc.sweep, true, paint)
            } else {
                if (sweepAngle > arc.start) {
                    paint.color = arc.color
                    canvas.drawArc(rect, START_ANGLE + arc.start, (sweepAngle - arc.start), true, paint)
                }
            }
        }
    }
}

private const val START_ANGLE = 225f

private class Arc(val start: Float, val end: Float, val sweep: Float, val color: Int)