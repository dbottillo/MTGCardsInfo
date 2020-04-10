package com.dbottillo.mtgsearchfree.sets

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class SetPickerActivity : BasicActivity(), SetPickerView {

    val list: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.set_list)
    }

    var adapter: SetsAdapter? = null

    @Inject lateinit var presenter: SetPickerPresenter

    override fun onCreate(bundle: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(bundle)
        setContentView(R.layout.activity_set_picker)

        setupToolbar(R.id.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

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
        adapter = SetsAdapter(sets, selectedPos) {
            presenter.setSelected(it)
        }
        list.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_set, menu)

        val mSearch = menu.findItem(R.id.action_search)

        val mSearchView = mSearch.actionView as SearchView
        mSearchView.queryHint = getString(R.string.menu_search_set)

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                presenter.search(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun close() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            false
        }
    }
}
