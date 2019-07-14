package com.dbottillo.mtgsearchfree.sets

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.view.Menu
import android.widget.ImageView
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.util.dpToPx
import dagger.android.AndroidInjection
import javax.inject.Inject

class SetPickerActivity : BasicActivity(), SetPickerView {

    val list: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.set_list)
    }

    val close: ImageView by lazy {
        findViewById<ImageView>(R.id.acton_close)
    }

    var adapter: SetsAdapter? = null

    @Inject lateinit var presenter: SetPickerPresenter

    override fun onCreate(bundle: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(bundle)
        setContentView(R.layout.activity_set_picker)

        setupToolbar(R.id.toolbar)

        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(Divider(ContextCompat.getDrawable(this, R.drawable.sets_divider)))

        close.setOnClickListener { finish() }

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

    class Divider(val drawable: Drawable?) : RecyclerView.ItemDecoration() {
        override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val dividerLeft = parent.context.dpToPx(16)
            val dividerRight = parent.width - dividerLeft

            val childCount = parent.childCount
            for (i in 0 until childCount - 1) {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val dividerTop = child.bottom + params.bottomMargin
                val dividerBottom = dividerTop + (drawable?.intrinsicHeight ?: 0)

                drawable?.let {
                    drawable.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                    drawable.draw(canvas)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
