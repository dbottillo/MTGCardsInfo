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
import com.dbottillo.mtgsearchfree.component.AndroidComponent
import com.dbottillo.mtgsearchfree.component.DaggerFilterComponent
import com.dbottillo.mtgsearchfree.helper.FilterHelper
import com.dbottillo.mtgsearchfree.helper.LOG
import com.dbottillo.mtgsearchfree.helper.TrackingHelper
import com.dbottillo.mtgsearchfree.modules.CardFilterModule
import com.dbottillo.mtgsearchfree.presenter.CardFilterPresenter
import com.dbottillo.mtgsearchfree.resources.CardFilter
import com.dbottillo.mtgsearchfree.view.CardFilterView
import com.dbottillo.mtgsearchfree.view.activities.FilterActivity
import javax.inject.Inject

class FilterFragment : BasicFragment(), View.OnClickListener, CardFilterView {

    @Inject lateinit var filterPresenter: CardFilterPresenter

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

        filterPresenter.loadFilter()
    }


    fun onToggleClicked(view: View) {
        val on = (view as ToggleButton).isChecked

        var label = ""

        when (view.id) {
            R.id.toggle_white -> filterPresenter.updateW(on)
            R.id.toggle_blue -> filterPresenter.updateU(on)
            R.id.toggle_black -> filterPresenter.updateB(on)
            R.id.toggle_red -> filterPresenter.updateR(on)
            R.id.toggle_green -> filterPresenter.updateG(on)
            R.id.toggle_artifact -> filterPresenter.updateArtifact(on)
            R.id.toggle_land -> filterPresenter.updateLand(on)
            R.id.toggle_eldrazi -> filterPresenter.updateEldrazi(on)
            R.id.toggle_common -> filterPresenter.updateCommon(on)
            R.id.toggle_uncommon -> filterPresenter.updateUncommon(on)
            R.id.toggle_rare -> filterPresenter.updateRare(on)
            R.id.toggle_myhtic -> filterPresenter.updateMythic(on)
        }
        /*
                TrackingHelper.getInstance(activity).trackEvent(TrackingHelper.UA_CATEGORY_FILTER, TrackingHelper.UA_ACTION_TOGGLE, label)

                editor?.apply()

                updateFilterUI()*/
        //filterPresenter.loadFilter()
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

    override fun filterLoaded(filter: CardFilter) {
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

        (view!!.findViewById(R.id.toggle_white) as ToggleButton).isChecked = filter.white
        (view!!.findViewById(R.id.toggle_blue) as ToggleButton).isChecked = filter.blue
        (view!!.findViewById(R.id.toggle_black) as ToggleButton).isChecked = filter.black
        (view!!.findViewById(R.id.toggle_red) as ToggleButton).isChecked = filter.red
        (view!!.findViewById(R.id.toggle_green) as ToggleButton).isChecked = filter.green
        (view!!.findViewById(R.id.toggle_artifact) as ToggleButton).isChecked = filter.artifact
        (view!!.findViewById(R.id.toggle_land) as ToggleButton).isChecked = filter.land
        (view!!.findViewById(R.id.toggle_eldrazi) as ToggleButton).isChecked = filter.eldrazi
        (view!!.findViewById(R.id.toggle_common) as ToggleButton).isChecked = filter.common
        (view!!.findViewById(R.id.toggle_uncommon) as ToggleButton).isChecked = filter.uncommon
        (view!!.findViewById(R.id.toggle_rare) as ToggleButton).isChecked = filter.rare
        (view!!.findViewById(R.id.toggle_myhtic) as ToggleButton).isChecked = filter.mythic

        val textFilter = view!!.findViewById(R.id.filter_text) as TextView
        LOG.e(filterString);
        textFilter.text = Html.fromHtml(filterString)
    }

    override fun getPageTrack(): String {
        return "/filter"
    }

    override fun setupComponent(appComponent: AndroidComponent) {
        DaggerFilterComponent.builder()
                .androidComponent(appComponent)
                .cardFilterModule(CardFilterModule(this))
                .build();
    }
}
