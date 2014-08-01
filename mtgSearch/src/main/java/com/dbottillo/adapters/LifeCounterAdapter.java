package com.dbottillo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.helper.FilterHelper;
import com.dbottillo.resources.MTGCard;
import com.dbottillo.resources.Player;

import java.util.List;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class LifeCounterAdapter extends BaseAdapter {

    public interface OnLifeCounterListener {
        void onRemovePlayer(int position);
        void onEditPlayer(int position);
        void onLifeCountChange(int position, int value);
        void onPoisonCountChange(int position, int value);
    }

    private List<Player> players;
    private Context context;
    private LayoutInflater inflater;
    private OnLifeCounterListener listener;

    public LifeCounterAdapter(Context context, List<Player> players, OnLifeCounterListener listener) {
        this.players = players;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Player getItem(int i) {
        return players.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final PlayerHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_life_counter, null);
            holder = new PlayerHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (PlayerHolder) convertView.getTag();
        }

        Player player = getItem(position);
        holder.name.setText(player.getName());
        holder.life.setText(player.getLife()+"");
        holder.poison.setText(player.getPoisonCount()+"");

        int color = R.color.life_counter_red;
        if (player.getLife() > 12){
            color = R.color.life_counter_green;
        } else if (player.getLife() > 7){
            color = R.color.life_counter_middle;
        }
        holder.life.setTextColor(context.getResources().getColor(color));
        color = R.color.life_counter_red;
        if (player.getPoisonCount() > 7){
            color = R.color.life_counter_green;
        } else if (player.getPoisonCount() > 3){
            color = R.color.life_counter_middle;
        }
        holder.poison.setTextColor(context.getResources().getColor(color));

        holder.edit.setTag(position);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onEditPlayer((Integer)remove.getTag());
            }
        });
        holder.remove.setTag(position);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onRemovePlayer((Integer)remove.getTag());
            }
        });
        holder.lifePlusOne.setTag(position);
        holder.lifePlusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onLifeCountChange((Integer)remove.getTag(), 1);
            }
        });
        holder.lifeMinusOne.setTag(position);
        holder.lifeMinusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onLifeCountChange((Integer)remove.getTag(), -1);
            }
        });
        holder.poisonPlusOne.setTag(position);
        holder.poisonPlusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onPoisonCountChange((Integer)remove.getTag(), 1);
            }
        });
        holder.poisonMinusOne.setTag(position);
        holder.poisonMinusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onPoisonCountChange((Integer)remove.getTag(), -1);
            }
        });


        return convertView;
    }

    class PlayerHolder {
        TextView name;
        TextView life;
        TextView poison;
        ImageButton edit;
        ImageButton remove;
        Button lifePlusOne;
        Button lifeMinusOne;
        Button poisonPlusOne;
        Button poisonMinusOne;

        PlayerHolder(View row){
            name = (TextView) row.findViewById(R.id.player_name);
            life = (TextView) row.findViewById(R.id.player_life);
            poison = (TextView) row.findViewById(R.id.player_poison);
            edit = (ImageButton) row.findViewById(R.id.player_edit);
            remove = (ImageButton) row.findViewById(R.id.player_remove);
            lifePlusOne = (Button) row.findViewById(R.id.btn_life_plus_one);
            lifeMinusOne = (Button) row.findViewById(R.id.btn_life_minus_one);
            poisonPlusOne = (Button) row.findViewById(R.id.btn_poison_plus_one);
            poisonMinusOne = (Button) row.findViewById(R.id.btn_poison_minus_one);
        }
    }

}
