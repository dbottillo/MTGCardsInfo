package com.dbottillo.mtgsearchfree.home

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import com.dbottillo.mtgsearchfree.util.themeColor

class BottomTabs : LinearLayout {

    interface BottomTabsListener {
        fun tabSelected(selection: Int)
    }

    private val scaleDefault: Float = 1.0f
    private val scaleSelected: Float = 1.2f
    private val alphaDefault: Float = 0.7f
    private val alphaSelected: Float = 1.0f

    private var homeTab: LinearLayout
    private var decksTab: LinearLayout
    private var savedTab: LinearLayout
    private var lifeCounterTab: LinearLayout

    private var homeTabImage: ImageView
    private var decksTabImage: ImageView
    private var savedTabImage: ImageView
    private var lifeCounterTabImage: ImageView

    private var currentSelection = 0
    private var listener: BottomTabsListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val view = inflate(context, R.layout.bottom_tabs, this)

        orientation = HORIZONTAL
        setBackgroundColor(context.themeColor(R.attr.toolbarSurfaceColor))

        homeTab = view.findViewById(R.id.home_tab)
        decksTab = view.findViewById(R.id.decks_tab)
        savedTab = view.findViewById(R.id.saved_tab)
        lifeCounterTab = view.findViewById(R.id.life_counter_tab)

        homeTabImage = view.findViewById(R.id.home_tab_image)
        decksTabImage = view.findViewById(R.id.decks_tab_image)
        savedTabImage = view.findViewById(R.id.saved_tab_image)
        lifeCounterTabImage = view.findViewById(R.id.life_counter_tab_image)

        homeTab.setOnClickListener { homeTabTapped() }
        decksTab.setOnClickListener { decksTabTapped() }
        savedTab.setOnClickListener { savedTabTapped() }
        lifeCounterTab.setOnClickListener { lifeCounterTabTapped() }

        refreshUI()
    }

    override fun onSaveInstanceState(): Parcelable {
        val parcelable = super.onSaveInstanceState()
        return BottomTabsState(parcelable, currentSelection)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val bottomTabState = state as BottomTabsState
        super.onRestoreInstanceState(state.superState)
        currentSelection = bottomTabState.selection
        refreshUI()
    }

    fun setBottomTabsListener(listener: BottomTabsListener) {
        this.listener = listener
    }

    fun setSelection(selected: Int) {
        currentSelection = selected
        refreshUI()
    }

    private fun refreshUI() {
        updateTab(homeTab, homeTabImage, selected = currentSelection == 0)
        updateTab(decksTab, decksTabImage, selected = currentSelection == 1)
        updateTab(savedTab, savedTabImage, selected = currentSelection == 2)
        updateTab(lifeCounterTab, lifeCounterTabImage, selected = currentSelection == 3)
    }

    private fun updateTab(tab: LinearLayout, image: ImageView, selected: Boolean) {
        if (selected) {
            tab.alpha = alphaSelected
            image.scaleX = scaleSelected
            image.scaleY = scaleSelected
        } else {
            tab.alpha = alphaDefault
            image.scaleX = scaleDefault
            image.scaleY = scaleDefault
        }
    }

    fun homeTabTapped() {
        setSelection(0)
        listener?.tabSelected(0)
    }

    fun decksTabTapped() {
        setSelection(1)
        listener?.tabSelected(1)
    }

    fun savedTabTapped() {
        setSelection(2)
        listener?.tabSelected(2)
    }

    fun lifeCounterTabTapped() {
        setSelection(3)
        listener?.tabSelected(3)
    }

    class BottomTabsState : BaseSavedState {

        val selection: Int

        constructor(state: Parcelable?, selection: Int) : super(state) {
            this.selection = selection
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(selection)
        }

        constructor(state: Parcel) : super(state) {
            this.selection = state.readInt()
        }

        companion object {

            @JvmField val CREATOR = object : Parcelable.Creator<BottomTabsState> {
                override fun createFromParcel(`in`: Parcel) = BottomTabsState(`in`)
                override fun newArray(size: Int): Array<BottomTabsState?> = arrayOfNulls(size)
            }
        }
    }
}
