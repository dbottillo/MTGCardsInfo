package com.dbottillo.mtgsearchfree.toolbarereveal

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import com.dbottillo.mtgsearchfree.util.AnimationUtil
import com.dbottillo.mtgsearchfree.util.dpToPx
import com.dbottillo.mtgsearchfree.util.setDarkStatusBar
import com.dbottillo.mtgsearchfree.util.setLightStatusBar
import java.lang.ref.WeakReference

/**
 * Helper class that contains the logic to reveal the toolbar after the user is scrolling the content
 * This class expect the concrete fragment to specify a scrollview with its content and this class will
 * handle the transition to reveal the toolbar during the scroll
 */
class ToolbarRevealScrollHelper @JvmOverloads constructor(
    baseFragment: BasicFragment,
    private val scrollviewID: Int,
    private val backgroundColor: Int,
    private val heightToolbar: Int,
    private val statusBarIncluded: Boolean,
    toolbarColor: Int = R.color.color_primary,
    statusBarColor: Int = R.color.color_primary
) : ViewTreeObserver.OnScrollChangedListener {

    private var mViewGroup: ViewGroup? = null
    private lateinit var alphaInterpolator: AnimationUtil.LinearInterpolator
    private lateinit var elevationInterpolator: AnimationUtil.LinearInterpolator
    private lateinit var translationTitle: AnimationUtil.LinearInterpolator
    private lateinit var toolbarBackgroundEvaluator: AnimationUtil.ArgbInterpolator
    private lateinit var statusBarColorEvaluator: AnimationUtil.ArgbInterpolator
    private lateinit var arrowToolbarEvaluator: AnimationUtil.ArgbInterpolator

    private var scrollingEnabled = false
    private var currentScroll = 0
    private var maximumScroll: Int = 0

    private val fragment: WeakReference<BasicFragment> = WeakReference(baseFragment)
    private val context: WeakReference<Context> = WeakReference<Context>(baseFragment.activity)
    private var toolbarColor: Int = 0
    private var statusBarColor: Int = 0

    init {
        baseFragment.context?.let {
            this.toolbarColor = ContextCompat.getColor(it, toolbarColor)
            this.statusBarColor = ContextCompat.getColor(it, statusBarColor)
        }
    }

    fun setToolbarColor(color: Int) {
        this.toolbarColor = color
    }

    fun setStatusBarColor(color: Int) {
        this.statusBarColor = color
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val instance = fragment.get()
        if (instance != null) {

            mViewGroup = view.findViewById(scrollviewID)
            instance.setupToolbar(view)
            maximumScroll = heightToolbar + view.context.dpToPx(OFFSET_MAXIMUM_SCROLL)
            setupTitleAnimation(instance, view.context)

            mViewGroup?.let {
                if (it is ScrollView) {
                    val mScrollViewContentLayout = it.getChildAt(0) as ViewGroup
                    mScrollViewContentLayout.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
                        val totalScroll = v.height - it.height
                        scrollingEnabled = totalScroll > 0
                        mScrollViewContentLayout.minimumHeight = if (scrollingEnabled) it.height + maximumScroll else 0
                        savedInstanceState?.let { recalculateCurrentScrollBasedOnOldMaximumScroll(it) }
                    }
                }
            }
            if (mViewGroup is RecyclerView) {
                scrollingEnabled = true

                if (savedInstanceState != null) {
                    recalculateCurrentScrollBasedOnOldMaximumScroll(savedInstanceState)
                }
            }
        }
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CURRENT_SCROLL, if (scrollingEnabled) currentScroll else 0)
        outState.putInt(MAXIMUM_SCROLL, maximumScroll)
    }

    fun onResume() {
        if (mViewGroup != null) {
            mViewGroup?.viewTreeObserver?.addOnScrollChangedListener(this)
        }
        refreshUI()
    }

    fun onPause() {
        val instance = fragment.get()
        val context = context.get()
        if (instance != null && context != null) {
            instance.activity.setDarkStatusBar()
            if (statusBarIncluded) {
                instance.activity?.window?.statusBarColor = ContextCompat.getColor(context, R.color.color_primary_dark)
            }
        }
        mViewGroup?.viewTreeObserver?.removeOnScrollChangedListener(this)
    }

    private fun setupTitleAnimation(baseFragment: BasicFragment, context: Context) {
        val translationStart = context.dpToPx(TITLE_TRANSLATION_START_Y)
        val translationEnd = 0
        baseFragment.toolbar.elevation = 0f
        baseFragment.toolbarTitle?.alpha = 0f
        baseFragment.toolbarTitle?.translationY = translationStart.toFloat()
        if (baseFragment.toolbar.navigationIcon != null) {
            baseFragment.toolbar.navigationIcon?.setTint(ContextCompat.getColor(context, R.color.color_primary))
        }
        setChildrenToolbarColor(baseFragment.toolbar, ContextCompat.getColor(context, R.color.color_primary))
        baseFragment.toolbar.overflowIcon?.setColorFilter(ContextCompat.getColor(context, R.color.color_primary), PorterDuff.Mode.SRC_IN)
        if (statusBarIncluded) {
            baseFragment.activity?.window?.statusBarColor = ContextCompat.getColor(context, R.color.main_bg)
            baseFragment.activity.setLightStatusBar()
        }

        alphaInterpolator = AnimationUtil.createLinearInterpolator().fromValue(0.0f).toValue(1.0f)
        elevationInterpolator = AnimationUtil.createLinearInterpolator().fromValue(0.0f).toValue(context.resources.getDimension(R.dimen.default_elevation_toolbar))
        translationTitle = AnimationUtil.createLinearInterpolator().fromValue(translationStart.toFloat()).toValue(translationEnd.toFloat())
        toolbarBackgroundEvaluator = AnimationUtil.createArgbInterpolator().fromValue(ContextCompat.getColor(context, backgroundColor)).toValue(toolbarColor)
        statusBarColorEvaluator = AnimationUtil.createArgbInterpolator().fromValue(ContextCompat.getColor(context, R.color.white)).toValue(statusBarColor)
        arrowToolbarEvaluator = AnimationUtil.createArgbInterpolator().fromValue(ContextCompat.getColor(context, R.color.color_primary)).toValue(ContextCompat.getColor(context, R.color.white))

        mViewGroup?.let {
            if (mViewGroup is ScrollView) {
                it.isVerticalFadingEdgeEnabled = false
                it.isHorizontalFadingEdgeEnabled = false
                it.setFadingEdgeLength(0)
                it.overScrollMode = ScrollView.OVER_SCROLL_NEVER
            }
        }
    }

    private fun setChildrenToolbarColor(viewGroup: ViewGroup, color: Int) {
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            if (view is ImageView) {
                view.setColorFilter(color)
            } else if (view is LinearLayout) {
                setChildrenToolbarColor(view, color)
            }
        }
    }

    private fun refreshUI() {
        fragment.get()?.let {
            val interval = calculateInterval()
            it.toolbarTitle?.alpha = alphaInterpolator.getInterpolation(interval)
            it.toolbar.elevation = elevationInterpolator.getInterpolation(interval)
            it.toolbarTitle?.translationY = translationTitle.getInterpolation(interval)
            it.toolbar.setBackgroundColor(toolbarBackgroundEvaluator.getInterpolation(interval))
            setChildrenToolbarColor(it.toolbar, arrowToolbarEvaluator.getInterpolation(interval))
            it.toolbar.navigationIcon?.setTint(arrowToolbarEvaluator.getInterpolation(interval))
            it.toolbar.overflowIcon?.setColorFilter(arrowToolbarEvaluator.getInterpolation(interval), PorterDuff.Mode.SRC_IN)
            if (statusBarIncluded) {
                it.activity?.window?.statusBarColor = statusBarColorEvaluator.getInterpolation(interval)
                if (interval <= THRESHOLD_STATUS_BAR_COLOR) {
                    it.activity.setLightStatusBar()
                } else {
                    it.activity.setDarkStatusBar()
                }
            }
        }
    }

    private fun calculateInterval(): Float {
        var interval = currentScroll.toFloat() / maximumScroll.toFloat()
        if (interval < 0.0f) {
            interval = 0.0f
        } else if (interval > 1.0f) {
            interval = 1.0f
        }
        if (!scrollingEnabled) {
            interval = 0.0f
        }
        return interval
    }

    private fun recalculateCurrentScrollBasedOnOldMaximumScroll(bundle: Bundle?) {
        bundle?.let {
            val oldCurrentScroll = it.getInt(CURRENT_SCROLL)
            val oldMaximumScroll = it.getInt(MAXIMUM_SCROLL)
            currentScroll = if (oldMaximumScroll == maximumScroll) {
                // after a backstack or a full restore we don't need to do anything
                oldCurrentScroll
            } else {
                // after a rotation change the offset it's different because the maximum scroll is different (depends on the app bar height)
                val interval = oldCurrentScroll.toFloat() / oldMaximumScroll.toFloat()
                (maximumScroll * interval).toInt()
            }
        }
        refreshUI()
    }

    override fun onScrollChanged() {
        mViewGroup?.let {
            currentScroll = if (mViewGroup is RecyclerView) {
                (mViewGroup as RecyclerView).computeVerticalScrollOffset()
            } else {
                it.scrollY
            }
        }
        refreshUI()
    }

    fun updateTitle(name: String?) {
        val instance = fragment.get()
        instance?.setTitle(name)
    }
}

private const val CURRENT_SCROLL = "currentScroll"
private const val MAXIMUM_SCROLL = "maximumScroll"
private const val THRESHOLD_STATUS_BAR_COLOR = 0.3f
private const val OFFSET_MAXIMUM_SCROLL = 100
private const val TITLE_TRANSLATION_START_Y = 30