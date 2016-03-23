package com.dbottillo.mtgsearchfree.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dbottillo.mtgsearchfree.R;
import com.dbottillo.mtgsearchfree.model.MTGSet;

import java.util.List;

public class SearchSetAdapter extends ArrayAdapter<MTGSet> {

    public SearchSetAdapter(Context context, List<MTGSet> sets) {
        super(context, -1, sets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SetHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_spinner_item, parent, false);
            holder = new SetHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (SetHolder) convertView.getTag();
        }
        holder.name.setText(getItem(position).getName());
        return convertView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final SetHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_spinner_dropdown_item, parent, false);
            holder = new SetHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (SetHolder) convertView.getTag();
        }
        holder.name.setText(getItem(position).getName());
        return convertView;
    }

    static class SetHolder {
        TextView name;

        SetHolder(View row) {
            name = (TextView) row.findViewById(android.R.id.text1);
        }
    }

}
