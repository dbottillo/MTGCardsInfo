package com.dbottillo.mtgsearchfree.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.dbottillo.mtgsearchfree.model.MTGSet

class SearchSetAdapter(context: Context, sets: List<MTGSet>) : ArrayAdapter<MTGSet>(context, -1, sets) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.row_spinner_item, parent, false)
        val holder: SetHolder
        if (convertView == null) {
            holder = SetHolder(view)
            view.tag = SetHolder(view)
            view.id = position
        } else {
            holder = convertView.tag as SetHolder
        }
        holder.name.text = getItem(position).name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.row_spinner_dropdown_item, parent, false)
        val holder: SetHolder
        if (convertView == null) {
            holder = SetHolder(view)
            view.tag = holder
            view.id = position
        } else {
            holder = convertView.tag as SetHolder
        }
        holder.name.text = getItem(position).name
        return view
    }

    class SetHolder(row: View) {
        var name = row.findViewById<TextView>(android.R.id.text1)
    }
}
