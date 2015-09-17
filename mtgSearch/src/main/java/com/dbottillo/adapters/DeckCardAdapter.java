package com.dbottillo.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.dbottillo.R;
import com.dbottillo.resources.MTGCard;

import java.util.ArrayList;
import java.util.List;

public class DeckCardAdapter extends RecyclerView.Adapter<CardViewHolder> {

    private final Context mContext;
    private List<MTGCard> cards;

    public void add(MTGCard card, int position) {
        position = position == -1 ? getItemCount() : position;
        cards.add(position, card);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        if (position < getItemCount()) {
            cards.remove(position);
            notifyItemRemoved(position);
        }
    }

    public DeckCardAdapter(Context context, ArrayList<MTGCard> cards) {
        mContext = context;
        this.cards = cards;
    }

    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.row_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int position) {
        final MTGCard card = cards.get(position);
        CardAdapterHelper.bindView(mContext, card, holder, false, true);
        holder.addToDeck.setVisibility(View.GONE);
        /*holder.addToDeck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(mContext, holder.addToDeck);
                final Menu menu = popupMenu.getMenu();

                popupMenu.getMenuInflater().inflate(R.menu.main, menu);
                //popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);

                *//*switch (Global.listMode) {
                    case Global.LIST_STYLE_NORMAL: {
                        menu.findItem(R.id.action_delete).setVisible(false);
                        break;
                    }
                    case Global.LIST_STYLE_FAVORITE: {
                        menu.findItem(R.id.action_add_to_favorite).setVisible(false);
                        break;
                    }
                    case Global.LIST_STYLE_WATCH_LIST: {
                        menu.findItem(R.id.action_add_to_watch_list).setVisible(false);
                        break;
                    }
                    case Global.LIST_STYLE_DOWNLOAD: {
                        menu.findItem(R.id.action_download).setVisible(false);
                        break;
                    }
                }*//*

                //itemPosition = (int) view.getTag(R.id.tag_item_position);
                popupMenu.show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }
}