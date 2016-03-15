package com.dbottillo.mtgsearchfree.view.views

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.bindView
import com.dbottillo.mtgsearchfree.R

class FilterPickerView : LinearLayout {

    interface OnFilterPickerListener {
        fun filterUpdated(type: CardFilter.TYPE, on: Boolean)
    }

    val filterText: TextView by bindView(R.id.filter_text)
    val arrow: ImageView by bindView(R.id.arrow_filter)

    val toggleW: ToggleButton by bindView(R.id.toggle_white)
    val toggleU: ToggleButton by bindView(R.id.toggle_blue)
    val toggleB: ToggleButton by bindView(R.id.toggle_black)
    val toggleR: ToggleButton by bindView(R.id.toggle_red)
    val toggleG: ToggleButton by bindView(R.id.toggle_green)

    val toggleLand: ToggleButton by bindView(R.id.toggle_land)
    val toggleArtifact: ToggleButton by bindView(R.id.toggle_artifact)
    val toggleEldrazi: ToggleButton by bindView(R.id.toggle_eldrazi)

    val toggleCommon: ToggleButton by bindView(R.id.toggle_common)
    val toggleUncommon: ToggleButton by bindView(R.id.toggle_uncommon)
    val toggleRare: ToggleButton by bindView(R.id.toggle_rare)
    val toggleMythic: ToggleButton by bindView(R.id.toggle_myhtic)
    private var listener: OnFilterPickerListener? = null

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, -1)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        orientation = LinearLayout.VERTICAL;

        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view = inflater.inflate(R.layout.view_filter_picker, this, true)

        ButterKnife.bind(this, view)
    }

    fun setFilterPickerListener(list: OnFilterPickerListener) {
        listener = list
    }

    fun setRotationArrow(angle: Float) {
        arrow.rotation = angle
    }

    fun refresh(filter: CardFilter) {
        var filterString = ""

        filterString += addEntryFilterString(filter.white, "W")
        filterString += addEntryFilterString(filter.blue, "U")
        filterString += addEntryFilterString(filter.black, "B")
        filterString += addEntryFilterString(filter.red, "R")
        filterString += addEntryFilterString(filter.green, "G")

        filterString += " - "
        filterString += addEntryFilterString(filter.eldrazi, "A")
        filterString += addEntryFilterString(filter.land, "L")
        filterString += addEntryFilterString(filter.eldrazi, "E")
        filterString += " - "

        filterString += addEntryFilterString(filter.common, "C")
        filterString += addEntryFilterString(filter.uncommon, "U")
        filterString += addEntryFilterString(filter.rare, "R")
        filterString += addEntryFilterString(filter.mythic, "M")

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

        filterText.text = Html.fromHtml(filterString)
    }

    fun addEntryFilterString(active: Boolean, text: String): String {
        var filterString = ""
        if (active) {
            filterString += "<font color=\"#FFFFFF\">$text</font>"
        } else {
            filterString += "<font color=\"#777777\">$text</font>"
        }
        filterString += "&nbsp;"
        return filterString
    }

    @OnClick(R.id.toggle_white, R.id.toggle_blue, R.id.toggle_black, R.id.toggle_red,
            R.id.toggle_green, R.id.toggle_artifact, R.id.toggle_land, R.id.toggle_eldrazi,
            R.id.toggle_common, R.id.toggle_uncommon,
            R.id.toggle_rare, R.id.toggle_myhtic)
    fun onToggleClicked(view: View) {
        val on = (view as ToggleButton).isChecked
        when (view.id) {
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
        }
    }

    fun onPanelSlide(offset: Float) {
        setRotationArrow(180 - (180 * offset));
    }

}

