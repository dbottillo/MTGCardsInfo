package com.dbottillo.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dbottillo.R;
import com.dbottillo.resources.Player;

import java.util.List;

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
    private boolean showPoison;

    public LifeCounterAdapter(Context context, List<Player> players, OnLifeCounterListener listener, boolean showPoison) {
        this.players = players;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
        this.showPoison = showPoison;
    }

    public void setShowPoison(boolean showPoison) {
        this.showPoison = showPoison;
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
            convertView = inflater.inflate(R.layout.row_life_counter, parent, false);
            holder = new PlayerHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (PlayerHolder) convertView.getTag();
        }

        Player player = getItem(position);
        if (player.getDiceResult() > 0) {
            holder.name.setText(context.getString(R.string.row_life_counter_name, player.getName(), player.getDiceResult()));
        } else {
            holder.name.setText(player.getName());
        }
        holder.life.setText(String.valueOf(player.getLife()));
        holder.poison.setText(String.valueOf(player.getPoisonCount()));

        int color = getColorOfPosition(position);
        holder.card.setCardBackgroundColor(color);

        /*int color = R.color.life_counter_red;
        if (player.getLife() > 12) {
            color = R.color.life_counter_green;
        } else if (player.getLife() > 7) {
            color = R.color.life_counter_middle;
        }
        holder.life.setTextColor(context.getResources().getColor(color));
        color = R.color.life_counter_red;
        if (player.getPoisonCount() > 7) {
            color = R.color.life_counter_green;
        } else if (player.getPoisonCount() > 3) {
            color = R.color.life_counter_middle;
        }
        holder.poison.setTextColor(context.getResources().getColor(color));*/

        holder.edit.setTag(position);
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onEditPlayer((Integer) remove.getTag());
            }
        });
        holder.remove.setTag(position);
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onRemovePlayer((Integer) remove.getTag());
            }
        });
        holder.lifePlusOne.setTag(position);
        holder.lifePlusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onLifeCountChange((Integer) remove.getTag(), 1);
            }
        });
        holder.lifeMinusOne.setTag(position);
        holder.lifeMinusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onLifeCountChange((Integer) remove.getTag(), -1);
            }
        });
        holder.lifePlusFive.setTag(position);
        holder.lifePlusFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onLifeCountChange((Integer) remove.getTag(), 5);
            }
        });
        holder.lifeMinusFive.setTag(position);
        holder.lifeMinusFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onLifeCountChange((Integer) remove.getTag(), -5);
            }
        });
        holder.poisonPlusOne.setTag(position);
        holder.poisonPlusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onPoisonCountChange((Integer) remove.getTag(), 1);
            }
        });
        holder.poisonMinusOne.setTag(position);
        holder.poisonMinusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View remove) {
                listener.onPoisonCountChange((Integer) remove.getTag(), -1);
            }
        });

        holder.poisonContainer.setVisibility(showPoison ? View.VISIBLE : View.GONE);

        return convertView;
    }

    private int getColorOfPosition(int position) {
        int mod = position % 5;
        if (mod == 0) {
            return context.getResources().getColor(R.color.player_1);
        }
        if (mod == 1){
            return context.getResources().getColor(R.color.player_2);
        }
        if (mod == 2) {
            return context.getResources().getColor(R.color.player_3);
        }
        if (mod == 3) {
            return context.getResources().getColor(R.color.player_4);
        }
        return context.getResources().getColor(R.color.player_5);
    }

    static class PlayerHolder {
        CardView card;
        TextView name;
        TextView life;
        TextView poison;
        View poisonContainer;
        ImageButton edit;
        ImageButton remove;
        Button lifeMinusOne;
        Button lifePlusOne;
        Button lifeMinusFive;
        Button lifePlusFive;
        Button poisonPlusOne;
        Button poisonMinusOne;

        PlayerHolder(View row) {
            card = (CardView) row.findViewById(R.id.life_counter_card);
            name = (TextView) row.findViewById(R.id.player_name);
            life = (TextView) row.findViewById(R.id.player_life);
            poison = (TextView) row.findViewById(R.id.player_poison);
            poisonContainer = row.findViewById(R.id.life_counter_poison_container);
            edit = (ImageButton) row.findViewById(R.id.player_edit);
            remove = (ImageButton) row.findViewById(R.id.player_remove);
            lifePlusOne = (Button) row.findViewById(R.id.btn_life_plus_one);
            lifeMinusOne = (Button) row.findViewById(R.id.btn_life_minus_one);
            lifePlusFive = (Button) row.findViewById(R.id.btn_life_plus_five);
            lifeMinusFive = (Button) row.findViewById(R.id.btn_life_minus_five);
            poisonPlusOne = (Button) row.findViewById(R.id.btn_poison_plus_one);
            poisonMinusOne = (Button) row.findViewById(R.id.btn_poison_minus_one);
        }
    }

}
