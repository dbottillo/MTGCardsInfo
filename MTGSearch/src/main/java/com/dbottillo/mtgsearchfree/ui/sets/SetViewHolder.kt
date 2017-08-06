package com.dbottillo.mtgsearchfree.ui.sets

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R

class SetViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
    val name: TextView = row.findViewById<TextView>(R.id.set_name)
}
