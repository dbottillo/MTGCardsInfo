package com.dbottillo.mtgsearchfree.ui.cardsCoonfigurator

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity
import javax.inject.Inject
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.util.LOG
import com.dbottillo.mtgsearchfree.view.CardFilterView
import com.dbottillo.mtgsearchfree.view.views.FilterPickerView

class CardsConfiguratorFragment : BottomSheetDialogFragment(), CardsConfiguratorView, FilterPickerView.OnFilterPickerListener {

    interface CardsConfiguratorListener{
        fun onConfigurationChange()
    }

    @Inject
    lateinit var presenter: CardsConfiguratorPresenter

    lateinit var filterPickerView: FilterPickerView

    lateinit var listener: CardsConfiguratorListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context as BasicActivity).mtgApp.uiGraph.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cards_configurator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterPickerView = view.findViewById(R.id.filter_view) as FilterPickerView
        filterPickerView.setFilterPickerListener(this)

        presenter.init(this)
    }

    override fun loadFilter(filter: CardFilter) {
        filterPickerView.refresh(filter)
        LOG.e("calling $listener")
        listener.onConfigurationChange()
    }

    override fun filterUpdated(type: CardFilter.TYPE, on: Boolean) {
        presenter.update(type, on)
    }

}
