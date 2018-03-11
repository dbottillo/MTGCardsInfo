package com.dbottillo.mtgsearchfree.ui.cardsConfigurator

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.CardFilter
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.ui.views.FilterPickerView
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class CardsConfiguratorFragment(val showFilter: Boolean = true,
                                val showOrder: Boolean = true) : BottomSheetDialogFragment(), CardsConfiguratorView, FilterPickerView.OnFilterPickerListener {

    interface CardsConfiguratorListener {
        fun onConfigurationChange()
    }

    @Inject
    lateinit var presenter: CardsConfiguratorPresenter

    lateinit var filterPickerView: FilterPickerView

    lateinit var listener: CardsConfiguratorListener

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cards_configurator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterPickerView = view.findViewById<FilterPickerView>(R.id.filter_view)
        filterPickerView.setFilterPickerListener(this)
        filterPickerView.configure(showFilter, showOrder)

        presenter.init(this)
    }

    override fun loadFilter(filter: CardFilter) {
        filterPickerView.refresh(filter)
        listener.onConfigurationChange()
    }

    override fun filterUpdated(type: CardFilter.TYPE, on: Boolean) {
        presenter.update(type, on)
    }

}
