package com.dbottillo.mtgsearchfree.ui.sets

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.view.activities.BasicActivity
import javax.inject.Inject

class SetPickerActivity : BasicActivity(), SetPickerView {

    val list: RecyclerView by lazy {
        findViewById(R.id.set_list) as RecyclerView
    }

    var adapter: SetsAdapter? = null

    @Inject
    lateinit var presenter: SetPickerPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mtgApp.uiGraph.inject(this)
        setContentView(R.layout.activity_set_picker)

        setupToolbar()

        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(this)

        presenter.init(this)
    }

    override fun getPageTrack(): String {
        return "/set_picker"
    }

    override fun onResume() {
        super.onResume()

        presenter.loadSets()
    }

    override fun showSets(sets: List<MTGSet>, selectedPos: Int) {
        adapter = SetsAdapter(sets, selectedPos, {
            presenter.setSelected(it)
        })
        list.adapter = adapter
    }

    override fun close() {
        finish()
    }
}
