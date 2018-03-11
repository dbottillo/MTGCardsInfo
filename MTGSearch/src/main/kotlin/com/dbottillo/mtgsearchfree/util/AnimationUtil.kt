package com.dbottillo.mtgsearchfree.util

import android.animation.*
import android.view.View

object AnimationUtil {

    private val DEFAULT_DURATION = 200

    fun animateHeight(view: View, target: Int): ValueAnimator? {
        if (view.height == target) {
            return null
        }
        val anim = ValueAnimator.ofInt(view.height, target)
        anim.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            view.setHeight(`val`)
        }
        anim.duration = DEFAULT_DURATION.toLong()
        anim.start()
        return anim
    }

    fun growView(view: View) {
        view.scaleX = 0.0f
        view.scaleY = 0.0f
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 1.0f))
        scaleUp.duration = 250
        scaleUp.start()
    }

    fun shrinkView(view: View) {
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f))
        scaleUp.duration = 250
        scaleUp.start()
    }

    fun createLinearInterpolator(): LinearInterpolator {
        return LinearInterpolator()
    }

    class LinearInterpolator : TimeInterpolator {

        private var mStartValue: Float = 0.toFloat()
        private var mEndValue: Float = 0.toFloat()

        fun fromValue(startValue: Float): LinearInterpolator {
            mStartValue = startValue
            return this
        }

        fun toValue(endValue: Float): LinearInterpolator {
            mEndValue = endValue
            return this
        }

        override fun getInterpolation(input: Float): Float {
            val amount = Math.abs(mEndValue - mStartValue)
            return if (mEndValue > mStartValue) {
                mStartValue + amount * input
            } else {
                mStartValue - amount * input
            }
        }
    }

    fun createArgbInterpolator(): ArgbInterpolator {
        return ArgbInterpolator()
    }

    class ArgbInterpolator {

        private var mStartValue: Int = 0
        private var mEndValue: Int = 0
        private val argbEvaluator: ArgbEvaluator = ArgbEvaluator()

        fun fromValue(startValue: Int): ArgbInterpolator {
            mStartValue = startValue
            return this
        }

        fun toValue(endValue: Int): ArgbInterpolator {
            mEndValue = endValue
            return this
        }

        fun getInterpolation(input: Float): Int {
            return argbEvaluator.evaluate(input, mStartValue, mEndValue) as Int
        }
    }

}
