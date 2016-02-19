package com.dbottillo.mtgsearchfree.view.fragments

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.helper.FilterHelper
import com.dbottillo.mtgsearchfree.helper.LOG
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter
import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.view.CardFilterView
import com.dbottillo.mtgsearchfree.view.activities.FilterActivity

class FilterFragment : BasicFragment, View.OnClickListener, CardFilterView {

    var filterPresenter: CardFilterPresenter? = null

    constructor() {
        filterPresenter = CardFilterPresenter(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val filterActivity = activity as FilterActivity
        filterActivity.slidingPanel?.setDragView(view?.findViewById(R.id.filter_draggable))
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.fragment_filter, container, false)

        (rootView.findViewById(R.id.btn_apply_filter) as Button).setOnClickListener(this)

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LOG.e("on view created " + filterPresenter.toString())

        filterPresenter?.loadFilter()
    }


    fun onToggleClicked(view: View) {
        val on = (view as ToggleButton).isChecked

        var label = ""

        when(view.id){
            R.id.toggle_white -> filterPresenter?.update(CardFilter.WHITE, on)
        }
/*
        val i = view.getId()
        if (i == R.id.toggle_white) {
            editor?.putBoolean(FilterHelper.FILTER_WHITE, on)
            label = "white"
        } else if (i == R.id.toggle_blue) {
            editor?.putBoolean(FilterHelper.FILTER_BLUE, on)
            label = "blue"
        } else if (i == R.id.toggle_black) {
            editor?.putBoolean(FilterHelper.FILTER_BLACK, on)
            label = "black"
        } else if (i == R.id.toggle_red) {
            editor?.putBoolean(FilterHelper.FILTER_RED, on)
            label = "red"
        } else if (i == R.id.toggle_green) {
            editor?.putBoolean(FilterHelper.FILTER_GREEN, on)
            label = "green"
        } else if (i == R.id.toggle_artifact) {
            editor?.putBoolean(FilterHelper.FILTER_ARTIFACT, on)
            label = "artifact"
        } else if (i == R.id.toggle_land) {
            editor?.putBoolean(FilterHelper.FILTER_LAND, on)
            label = "land"
        } else if (i == R.id.toggle_eldrazi) {
            editor?.putBoolean(FilterHelper.FILTER_ELDRAZI, on)
            label = "land"
        } else if (i == R.id.toggle_common) {
            editor?.putBoolean(FilterHelper.FILTER_COMMON, on)
            label = "common"
        } else if (i == R.id.toggle_uncommon) {
            editor?.putBoolean(FilterHelper.FILTER_UNCOMMON, on)
            label = "uncommon"
        } else if (i == R.id.toggle_rare) {
            editor?.putBoolean(FilterHelper.FILTER_RARE, on)
            label = "rare"
        } else if (i == R.id.toggle_myhtic) {
            editor?.putBoolean(FilterHelper.FILTER_MYHTIC, on)
            label = "mythic"
        }

        TrackingHelper.getInstance(activity).trackEvent(TrackingHelper.UA_CATEGORY_FILTER, TrackingHelper.UA_ACTION_TOGGLE, label)

        editor?.apply()

        updateFilterUI()*/
        filterPresenter?.loadFilter()
    }

    private fun updateFilterUI() {
        TrackingHelper.getInstance(activity).trackEvent(TrackingHelper.UA_CATEGORY_FILTER, "update", "")
        var filterString = ""

        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_WHITE, true), "W")
        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_BLUE, true), "U")
        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_BLACK, true), "B")
        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_RED, true), "R")
        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_GREEN, true), "G")

        filterString += " - "
        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_ARTIFACT, true), "A")
        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_LAND, true), "L")
        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_ELDRAZI, true), "E")
        filterString += "  - "

        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_COMMON, true), "C")
        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_UNCOMMON, true), "U")
        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_RARE, true), "R")
        filterString += addEntryFilterString(sharedPreferences!!.getBoolean(FilterHelper.FILTER_MYHTIC, true), "M")

        (view!!.findViewById(R.id.toggle_white) as ToggleButton).isChecked = sharedPreferences!!?.getBoolean(FilterHelper.FILTER_WHITE, true)
        (view!!.findViewById(R.id.toggle_blue) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_BLUE, true)
        (view!!.findViewById(R.id.toggle_black) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_BLACK, true)
        (view!!.findViewById(R.id.toggle_red) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_RED, true)
        (view!!.findViewById(R.id.toggle_green) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_GREEN, true)
        (view!!.findViewById(R.id.toggle_artifact) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_ARTIFACT, true)
        (view!!.findViewById(R.id.toggle_land) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_LAND, true)
        (view!!.findViewById(R.id.toggle_eldrazi) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_ELDRAZI, true)
        (view!!.findViewById(R.id.toggle_common) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_COMMON, true)
        (view!!.findViewById(R.id.toggle_uncommon) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_UNCOMMON, true)
        (view!!.findViewById(R.id.toggle_rare) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_RARE, true)
        (view!!.findViewById(R.id.toggle_myhtic) as ToggleButton).isChecked = sharedPreferences!!.getBoolean(FilterHelper.FILTER_MYHTIC, true)

        val textFilter = view!!.findViewById(R.id.filter_text) as TextView
        textFilter.text = Html.fromHtml(filterString)
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

    override fun onClick(v: View) {
        TrackingHelper.getInstance(v.context).trackEvent(TrackingHelper.UA_CATEGORY_FILTER, TrackingHelper.UA_ACTION_CLOSE, "")
        val filterActivity = activity as FilterActivity
        filterActivity.slidingPanel?.collapsePane()
    }

    override fun filterLoaded() {
        var filterString = ""

        filterString += addEntryFilterString(CardFilter.WHITE.on, "W")
        filterString += addEntryFilterString(CardFilter.BLUE.on, "U")
        filterString += addEntryFilterString(CardFilter.BLACK.on, "B")
        filterString += addEntryFilterString(CardFilter.RED.on, "R")
        filterString += addEntryFilterString(CardFilter.GREEN.on, "G")

        filterString += " - "
        filterString += addEntryFilterString(CardFilter.ARTIFACT.on, "A")
        filterString += addEntryFilterString(CardFilter.LAND.on, "L")
        filterString += addEntryFilterString(CardFilter.ELDRAZI.on, "E")
        filterString += " - "

        filterString += addEntryFilterString(CardFilter.COMMON.on, "C")
        filterString += addEntryFilterString(CardFilter.UNCOMMON.on, "U")
        filterString += addEntryFilterString(CardFilter.RARE.on, "R")
        filterString += addEntryFilterString(CardFilter.MYTHIC.on, "M")

        (view!!.findViewById(R.id.toggle_white) as ToggleButton).isChecked = CardFilter.WHITE.on
        (view!!.findViewById(R.id.toggle_blue) as ToggleButton).isChecked = CardFilter.BLUE.on
        (view!!.findViewById(R.id.toggle_black) as ToggleButton).isChecked = CardFilter.BLACK.on
        (view!!.findViewById(R.id.toggle_red) as ToggleButton).isChecked = CardFilter.RED.on
        (view!!.findViewById(R.id.toggle_green) as ToggleButton).isChecked = CardFilter.GREEN.on
        (view!!.findViewById(R.id.toggle_artifact) as ToggleButton).isChecked = CardFilter.ARTIFACT.on
        (view!!.findViewById(R.id.toggle_land) as ToggleButton).isChecked = CardFilter.LAND.on
        (view!!.findViewById(R.id.toggle_eldrazi) as ToggleButton).isChecked = CardFilter.ELDRAZI.on
        (view!!.findViewById(R.id.toggle_common) as ToggleButton).isChecked = CardFilter.COMMON.on
        (view!!.findViewById(R.id.toggle_uncommon) as ToggleButton).isChecked = CardFilter.UNCOMMON.on
        (view!!.findViewById(R.id.toggle_rare) as ToggleButton).isChecked = CardFilter.RARE.on
        (view!!.findViewById(R.id.toggle_myhtic) as ToggleButton).isChecked = CardFilter.MYTHIC.on

        val textFilter = view!!.findViewById(R.id.filter_text) as TextView
        LOG.e(filterString);
        textFilter.text = Html.fromHtml(filterString)
    }

    override fun getPageTrack(): String {
        return "/filter"
    }
}
