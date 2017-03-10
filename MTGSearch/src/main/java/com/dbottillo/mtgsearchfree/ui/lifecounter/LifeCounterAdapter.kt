package com.dbottillo.mtgsearchfree.ui.lifecounter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.Player

class LifeCounterAdapter(val players: List<Player>, val listener: OnLifeCounterListener, var showPoisonCounter: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private val TYPE_HEADER: Int = 0
        private val TYPE_PLAYER: Int = 1
        private val TYPE_FOOTER: Int = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        if (viewType == TYPE_HEADER) {
            return HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.life_counter_header, parent, false))
        }
        if (viewType == TYPE_FOOTER) {
            return FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.life_counter_footer, parent, false))
        }
        return PlayerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_life_counter, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_PLAYER) {
            holder as PlayerViewHolder
            val player = players[position - 1]

            if (player.diceResult > 0) {
                holder.name.text = holder.itemView.context.getString(R.string.row_life_counter_name, player.name, player.diceResult)
            } else {
                holder.name.text = player.name
            }
            holder.life.text = player.life.toString()
            holder.poison.text = player.poisonCount.toString()

            val color = getColorOfPosition(holder.itemView.context, position)
            holder.card.setCardBackgroundColor(color)

            holder.edit.setOnClickListener { listener.onEditPlayer(player) }
            holder.remove.setOnClickListener { listener.onRemovePlayer(player) }
            holder.lifePlusOne.setOnClickListener { listener.onLifeCountChange(player, 1) }
            holder.lifeMinusOne.setOnClickListener { listener.onLifeCountChange(player, -1) }
            holder.lifePlusFive.setOnClickListener { listener.onLifeCountChange(player, 5) }
            holder.lifeMinusFive.setOnClickListener { listener.onLifeCountChange(player, -5) }
            holder.poisonPlusOne.setOnClickListener { listener.onPoisonCountChange(player, 1) }
            holder.poisonMinusOne.setOnClickListener { listener.onPoisonCountChange(player, -1) }

            holder.poisonContainer.visibility = if (showPoisonCounter) View.VISIBLE else View.GONE
        }
    }

    override fun getItemCount(): Int {
        return players.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> TYPE_HEADER
            players.size + 1 -> TYPE_FOOTER
            else -> TYPE_PLAYER
        }
    }

    class HeaderViewHolder(val row: View) : RecyclerView.ViewHolder(row)

    class FooterViewHolder(val row: View) : RecyclerView.ViewHolder(row)

    class PlayerViewHolder(val row: View) : RecyclerView.ViewHolder(row) {

        val card: CardView = row.findViewById(R.id.life_counter_card) as CardView
        val name: TextView = row.findViewById(R.id.player_name) as TextView
        val life: TextView = row.findViewById(R.id.player_life) as TextView
        val poison: TextView = row.findViewById(R.id.player_poison) as TextView
        val poisonContainer: View = row.findViewById(R.id.life_counter_poison_container)
        val edit: ImageButton = row.findViewById(R.id.player_edit) as ImageButton
        val remove: ImageButton = row.findViewById(R.id.player_remove) as ImageButton
        val lifePlusOne: Button = row.findViewById(R.id.btn_life_plus_one) as Button
        val lifeMinusOne: Button = row.findViewById(R.id.btn_life_minus_one) as Button
        val lifePlusFive: Button = row.findViewById(R.id.btn_life_plus_five) as Button
        val lifeMinusFive: Button = row.findViewById(R.id.btn_life_minus_five) as Button
        val poisonPlusOne: Button = row.findViewById(R.id.btn_poison_plus_one) as Button
        val poisonMinusOne: Button = row.findViewById(R.id.btn_poison_minus_one) as Button
    }

    private fun getColorOfPosition(context: Context, position: Int): Int {
        val mod = position % 5
        if (mod == 0) {
            return ContextCompat.getColor(context, R.color.player_1)
        }
        if (mod == 1) {
            return ContextCompat.getColor(context, R.color.player_2)
        }
        if (mod == 2) {
            return ContextCompat.getColor(context, R.color.player_3)
        }
        if (mod == 3) {
            return ContextCompat.getColor(context, R.color.player_4)
        }
        return ContextCompat.getColor(context, R.color.player_5)
    }

    fun setShowPoison(showPoison: Boolean) {
        showPoisonCounter = showPoison
    }
}
