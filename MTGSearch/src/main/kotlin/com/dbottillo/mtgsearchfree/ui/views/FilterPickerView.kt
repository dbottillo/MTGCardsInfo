package com.dbottillo.mtgsearchfree.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ToggleButton

import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.util.LOG

class FilterPickerView(context: Context, attrs: AttributeSet?, defStyle: Int) : LinearLayout(context, attrs, defStyle), View.OnClickListener {

    private val filterPanelContainer by lazy(LazyThreadSafetyMode.NONE) { findViewById<View>(R.id.filter_panel_container) }
    private val filterTitle by lazy(LazyThreadSafetyMode.NONE) { findViewById<View>(R.id.filter_title) }
    private val filterDivisor by lazy(LazyThreadSafetyMode.NONE) { findViewById<View>(R.id.filter_divisor) }

    private val toggleW by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_white) }
    private val toggleU by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_blue) }
    private val toggleB by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_black) }
    private val toggleR by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_red) }
    private val toggleG by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_green) }

    private val toggleLand by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_land) }
    private val toggleArtifact by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_artifact) }
    private val toggleEldrazi by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_eldrazi) }

    private val toggleCommon by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_common) }
    private val toggleUncommon by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_uncommon) }
    private val toggleRare by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_rare) }
    private val toggleMythic by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_myhtic) }

    private val toggleOrder by lazy(LazyThreadSafetyMode.NONE) { findViewById<ToggleButton>(R.id.toggle_order) }
    private val orderTitle by lazy(LazyThreadSafetyMode.NONE) { findViewById<View>(R.id.order_title) }
    private val orderDivisor by lazy(LazyThreadSafetyMode.NONE) { findViewById<View>(R.id.order_divisor) }

    private var listener: OnFilterPickerListener? = null

    interface OnFilterPickerListener {
        fun filterUpdated(type: CardFilter.TYPE, on: Boolean)
    }

    @JvmOverloads constructor(ctx: Context, attrs: AttributeSet? = null) : this(ctx, attrs, -1) {}

    init {
        orientation = LinearLayout.VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_filter_picker, this)
        toggleW.setOnClickListener(this)
        toggleU.setOnClickListener(this)
        toggleR.setOnClickListener(this)
        toggleG.setOnClickListener(this)
        toggleB.setOnClickListener(this)
        toggleLand.setOnClickListener(this)
        toggleArtifact.setOnClickListener(this)
        toggleEldrazi.setOnClickListener(this)
        toggleCommon.setOnClickListener(this)
        toggleUncommon.setOnClickListener(this)
        toggleRare.setOnClickListener(this)
        toggleMythic.setOnClickListener(this)
        toggleOrder.setOnClickListener(this)
    }

    fun setFilterPickerListener(list: OnFilterPickerListener) {
        listener = list
    }

    fun refresh(filter: CardFilter) {
        LOG.d()
        toggleW.isChecked = filter.white
        toggleU.isChecked = filter.blue
        toggleB.isChecked = filter.black
        toggleR.isChecked = filter.red
        toggleG.isChecked = filter.green
        toggleArtifact.isChecked = filter.artifact
        toggleLand.isChecked = filter.land
        toggleEldrazi.isChecked = filter.eldrazi
        toggleCommon.isChecked = filter.common
        toggleUncommon.isChecked = filter.uncommon
        toggleRare.isChecked = filter.rare
        toggleMythic.isChecked = filter.mythic
        toggleOrder.isChecked = filter.sortSetNumber
    }

    override fun onClick(view: View) {
        LOG.d()
        val on = (view as ToggleButton).isChecked
        when (view.getId()) {
            R.id.toggle_white -> listener?.filterUpdated(CardFilter.TYPE.WHITE, on)
            R.id.toggle_blue -> listener?.filterUpdated(CardFilter.TYPE.BLUE, on)
            R.id.toggle_black -> listener?.filterUpdated(CardFilter.TYPE.BLACK, on)
            R.id.toggle_red -> listener?.filterUpdated(CardFilter.TYPE.RED, on)
            R.id.toggle_green -> listener?.filterUpdated(CardFilter.TYPE.GREEN, on)
            R.id.toggle_artifact -> listener?.filterUpdated(CardFilter.TYPE.ARTIFACT, on)
            R.id.toggle_land -> listener?.filterUpdated(CardFilter.TYPE.LAND, on)
            R.id.toggle_eldrazi -> listener?.filterUpdated(CardFilter.TYPE.ELDRAZI, on)
            R.id.toggle_common -> listener?.filterUpdated(CardFilter.TYPE.COMMON, on)
            R.id.toggle_uncommon -> listener?.filterUpdated(CardFilter.TYPE.UNCOMMON, on)
            R.id.toggle_rare -> listener?.filterUpdated(CardFilter.TYPE.RARE, on)
            R.id.toggle_myhtic -> listener?.filterUpdated(CardFilter.TYPE.MYTHIC, on)
            R.id.toggle_order -> listener?.filterUpdated(CardFilter.TYPE.SORT_SET_NUMBER, on)
            else -> {
            }
        }
    }

    fun configure(showFilter: Boolean, showOrder: Boolean) {
        filterPanelContainer.visibility = if (showFilter) View.VISIBLE else View.GONE
        filterTitle.visibility = if (showFilter) View.VISIBLE else View.GONE
        filterDivisor.visibility = if (showFilter) View.VISIBLE else View.GONE
        toggleOrder.visibility = if (showOrder) View.VISIBLE else View.GONE
        orderDivisor.visibility = if (showOrder) View.VISIBLE else View.GONE
        orderTitle.visibility = if (showOrder) View.VISIBLE else View.GONE
    }
}
