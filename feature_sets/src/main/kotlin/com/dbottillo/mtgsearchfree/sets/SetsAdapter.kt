package com.dbottillo.mtgsearchfree.sets

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbottillo.mtgsearchfree.model.MTGSet

class SetsAdapter(
    val sets: List<MTGSet>,
    val currentPos: Int,
    val selected: (set: MTGSet) -> Unit
) : RecyclerView.Adapter<SetViewHolder>() {

    override fun getItemCount(): Int {
        return sets.size
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        holder.name.text = sets[position].name

        holder.name.setTextColor(ContextCompat.getColor(holder.itemView.context, if (position == currentPos) R.color.color_accent else R.color.color_primary))
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
