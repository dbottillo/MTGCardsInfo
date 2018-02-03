package com.dbottillo.mtgsearchfree.ui.about

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.BasicActivity
import com.dbottillo.mtgsearchfree.util.hide
import com.dbottillo.mtgsearchfree.util.show
import javax.inject.Inject


class ReleaseNoteActivity : BasicActivity(), ReleaseNoteView {

    @Inject lateinit var presenter: ReleaseNotePresenter

    private val releaseNoteList: RecyclerView by lazy(LazyThreadSafetyMode.NONE) { findViewById<RecyclerView>(R.id.release_note_list) }
    private val emptyView: TextView by lazy(LazyThreadSafetyMode.NONE) { findViewById<TextView>(R.id.empty_view) }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        setContentView(R.layout.activity_release_note)

        findViewById<Toolbar>(R.id.toolbar).also {
            setSupportActionBar(it)
            supportActionBar?.setTitle(R.string.action_release_note)
            it.setNavigationIcon(R.drawable.ic_close)
        }

        mtgApp.uiGraph.inject(this)

        presenter.init(this)
        presenter.load()

        releaseNoteList.setHasFixedSize(true)
        releaseNoteList.layoutManager = LinearLayoutManager(this)
        releaseNoteList.addItemDecoration(ReleaseNoteFooter(resources.getDimensionPixelSize(R.dimen.footer_height)))
    }

    override fun getPageTrack(): String? {
        return "/release-note"
    }

    override fun showError(message: String) {
        releaseNoteList.hide()
        emptyView.show()
        emptyView.text = message
    }

    override fun showItems(list: List<ReleaseNoteItem>) {
        emptyView.hide()
        releaseNoteList.show()
        releaseNoteList.adapter = ReleaseNoteAdapter(list)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

class ReleaseNoteAdapter(val items: List<ReleaseNoteItem>) : RecyclerView.Adapter<ReleaseNoteHolder>() {
    override fun onBindViewHolder(holder: ReleaseNoteHolder, position: Int) {
        holder.title.text = items[position].title
        holder.text.text = items[position].lines.joinToString(separator = "\n") { "- $it" }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReleaseNoteHolder {
        return ReleaseNoteHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_release_note, parent, false))
    }

    override fun getItemCount() = items.size

}

class ReleaseNoteHolder(view: View) : RecyclerView.ViewHolder(view) {
    var title: TextView = view.findViewById(R.id.release_note_title)
    var text: TextView = view.findViewById(R.id.release_note_text)
}

class ReleaseNoteFooter(val height: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildAdapterPosition(view) == parent.adapter.itemCount - 1) {
            outRect.set(0, 0, 0, height)
        } else {
            outRect.setEmpty()
        }
    }
}