package com.dbottillo.mtgsearchfree.toolbarereveal

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.AttrRes
import com.dbottillo.mtgsearchfree.core.R
import com.dbottillo.mtgsearchfree.ui.BasicFragment
import com.dbottillo.mtgsearchfree.util.AnimationUtil
import com.dbottillo.mtgsearchfree.util.dpToPx
import com.dbottillo.mtgsearchfree.util.setDarkStatusBar
import com.dbottillo.mtgsearchfree.util.setLightStatusBar
import com.dbottillo.mtgsearchfree.util.themeColor
import java.lang.ref.WeakReference

/**
 * Helper class that contains the logic to reveal the toolbar after the user is scrolling the content
 * This class expect the concrete fragment to specify a scrollview with its content and this class will
 * handle the transition to reveal the toolbar during the scroll
 */
@Suppress("LargeClass")
class ToolbarRevealScrollHelper @JvmOverloads constructor(
    baseFragment: BasicFragment,
    private val scrollviewID: Int,
    private val toolbarId: Int,
    private val toolbarTitleId: Int,
    private val heightToolbar: Int
) : ViewTreeObserver.OnScrollChangedListener {

    private var mViewGroup: ViewGroup? = null
    private lateinit var alphaInterpolator: AnimationUtil.LinearInterpolator
    private lateinit var elevationInterpolator: AnimationUtil.LinearInterpolator
    private lateinit var translationTitle: AnimationUtil.LinearInterpolator

    private var scrollingEnabled = false
    private var currentScroll = 0
    private var maximumScroll: Int = 0

    private val fragment: WeakReference<BasicFragment> = WeakReference(baseFragment)

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val instance = fragment.get()
        if (instance != null) {

            mViewGroup = view.findViewById(scrollviewID)
            instance.setupToolbar(view, toolbarId, toolbarTitleId)
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
        mViewGroup?.viewTreeObserver?.removeOnScrollChangedListener(this)
    }

    private fun setupTitleAnimation(baseFragment: BasicFragment, context: Context) {
        val translationStart = context.dpToPx(TITLE_TRANSLATION_START_Y)
        val translationEnd = 0
        baseFragment.toolbar.elevation = 0f
        baseFragment.toolbarTitle?.alpha = 0f
        baseFragment.toolbarTitle?.translationY = translationStart.toFloat()
        alphaInterpolator = AnimationUtil.createLinearInterpolator().fromValue(0.0f).toValue(1.0f)
        elevationInterpolator = AnimationUtil.createLinearInterpolator().fromValue(0.0f).toValue(context.resources.getDimension(R.dimen.default_elevation_toolbar))
        translationTitle = AnimationUtil.createLinearInterpolator().fromValue(translationStart.toFloat()).toValue(translationEnd.toFloat())

        mViewGroup?.let {
            if (mViewGroup is ScrollView) {
                it.isVerticalFadingEdgeEnabled = false
                it.isHorizontalFadingEdgeEnabled = false
                it.setFadingEdgeLength(0)
                it.overScrollMode = ScrollView.OVER_SCROLL_NEVER
            }
        }
    }
    private fun refreshUI() {
        fragment.get()?.let {
            val interval = calculateInterval()
            it.toolbarTitle?.alpha = alphaInterpolator.getInterpolation(interval)
            it.toolbar.elevation = elevationInterpolator.getInterpolation(interval)
            it.toolbarTitle?.translationY = translationTitle.getInterpolation(interval)
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
private const val OFFSET_MAXIMUM_SCROLL = 100
private const val TITLE_TRANSLATION_START_Y = 30