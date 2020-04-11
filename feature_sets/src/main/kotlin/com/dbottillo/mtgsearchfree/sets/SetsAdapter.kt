package com.dbottillo.mtgsearchfree.sets

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbottillo.mtgsearchfree.model.MTGSet
import com.dbottillo.mtgsearchfree.util.themeColor

class SetsAdapter(
    private val sets: List<MTGSet>,
    private val currentPos: Int,
    private val selected: (set: MTGSet) -> Unit
) : RecyclerView.Adapter<SetViewHolder>() {

    override fun getItemCount(): Int {
        return sets.size
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        holder.name.text = sets[position].name

        val attr = if (position == currentPos) R.attr.colorSecondary else R.attr.colorPrimary
        holder.name.setTextColor(holder.itemView.context.themeColor(attr))
        holder.name.setTypeface(null, if (position == currentPos) Typeface.BOLD else Typeface.NORMAL)
        holder.itemView.setOnClickListener {
            if (position != currentPos) {
                selected(sets[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): SetViewHolder {
        return SetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_set, parent, false))
    }
}
