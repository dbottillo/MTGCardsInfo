package com.dbottillo.mtgsearchfree.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.resources.MTGSet;

import java.util.List;

public class GameSetAdapter extends BaseAdapter {

    private List<MTGSet> mtgSets;
    private Context context;
    private LayoutInflater inflater;
    private int current = -1;

    public GameSetAdapter(Context context, List<MTGSet> mtgSets) {
        this.mtgSets = mtgSets;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mtgSets.size();
    }

    @Override
    public MTGSet getItem(int i) {
        return mtgSets.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getId();
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final SetHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_set, parent, false);
            holder = new SetHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (SetHolder) convertView.getTag();
        }

        MTGSet set = getItem(position);
        holder.name.setTextColor(context.getResources().getColor(position == current ? R.color.main : R.color.dark_grey));
        holder.name.setTypeface(null, position == current ? Typeface.BOLD : Typeface.NORMAL);
        holder.name.setText(set.getName());

        return convertView;
    }

    static class SetHolder {
        TextView name;

        SetHolder(View row) {
            name = (TextView) row.findViewById(R.id.set_name);
        }
    }

}
