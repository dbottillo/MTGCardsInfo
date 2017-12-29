package com.dbottillo.mtgsearchfree.ui.cards

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.ui.views.MTGLoader

abstract class CardViewHolder(row: View) : RecyclerView.ViewHolder(row) {
    var parent: View = row.findViewById(R.id.card_parent)
}

class ListCardViewHolder(row: View) : CardViewHolder(row){
    var name: TextView = row.findViewById(R.id.card_name)
    var setName: TextView = row.findViewById(R.id.card_set_name)
    var rarity: TextView = row.findViewById(R.id.card_rarity)
    var cost: TextView = row.findViewById(R.id.card_cost)
    var indicator: View = row.findViewById(R.id.card_indicator)
    var more: ImageButton = row.findViewById(R.id.card_more_option)
}

class GridCardViewHolder(row: View): CardViewHolder(row){
    var image: ImageView = row.findViewById(R.id.grid_item_card_image)
    var loader: MTGLoader = row.findViewById(R.id.grid_item_card_loader)
}