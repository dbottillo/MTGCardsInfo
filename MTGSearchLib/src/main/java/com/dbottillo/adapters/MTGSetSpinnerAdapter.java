package com.dbottillo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dbottillo.mtgsearch.R;
import com.dbottillo.resources.MTGSet;

import java.util.List;

/**
 * Created by danielebottillo on 23/02/2014.
 */
public class MTGSetSpinnerAdapter extends BaseAdapter {

    private List<MTGSet> mtgSets;
    private Context context;
    private LayoutInflater inflater;

    public MTGSetSpinnerAdapter(Context context, List<MTGSet> mtgSets) {
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

    public View getView(int position, View convertView, ViewGroup parent) {
        final SetHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_set, null);
            holder = new SetHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (SetHolder) convertView.getTag();
        }

        MTGSet set = getItem(position);
        holder.name.setText(set.getName());

        return convertView;
    }


    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final SetHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_set, null);
            holder = new SetHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (SetHolder) convertView.getTag();
        }

        MTGSet set = getItem(position);
        holder.name.setText(set.getName());

        return convertView;
    }

    class SetHolder {
        TextView name;

        SetHolder(View row){
            name = (TextView) row.findViewById(R.id.set_name);
        }
    }

}
