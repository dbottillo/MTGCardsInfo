package com.dbottillo.mtgsearchfree.ui.sets

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.util.UIUtil
import javax.inject.Inject

class SetPickerActivity : BasicActivity(), SetPickerView {

    val list: RecyclerView by lazy {
        findViewById(R.id.set_list) as RecyclerView
    }

    var adapter: SetsAdapter? = null

    @Inject
    lateinit var presenter: SetPickerPresenter

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        mtgApp.uiGraph.inject(this)
        setContentView(R.layout.activity_set_picker)

        setupToolbar()

        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(this)
        list.addItemDecoration(Divider(ContextCompat.getDrawable(this, R.drawable.sets_divider)))

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

    class Divider(val drawable: Drawable) : RecyclerView.ItemDecoration(){
        override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State?){
            val dividerLeft = UIUtil.dpToPx(parent.context, 16)
            val dividerRight = parent.width - dividerLeft

            val childCount = parent.childCount
            for (i in 0..childCount - 1 - 1) {
                val child = parent.getChildAt(i)

                val params = child.layoutParams as RecyclerView.LayoutParams

                val dividerTop = child.bottom + params.bottomMargin
                val dividerBottom = dividerTop + drawable.intrinsicHeight

                drawable.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
                drawable.draw(canvas)
            }
        }
    }
}
