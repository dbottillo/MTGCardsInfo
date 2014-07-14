package com.dbottillo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbottillo.BuildConfig;
import com.dbottillo.R;

/**
 * Created by danielebottillo on 14/07/2014.
 */
public class LeftMenuAdapter extends BaseAdapter {

    public enum LeftMenuItem {
        HOME(0, R.string.action_home, R.drawable.left_home),
        FAVOURITE(1, R.string.action_saved, R.drawable.left_saved),
        FORCE_UPDATE(2, R.string.action_update_database, R.drawable.left_update),
        ABOUT(3, R.string.action_about, R.drawable.left_info),
        CREATE_DB(4, R.string.action_create_db, -1);

        private int id;
        private int title;
        private int icon;

        LeftMenuItem(int id, int title, int icon){
            this.id = id;
            this.title = title;
            this.icon =  icon;
        }

        public int getPosition(){
            return id;
        }
    }

    private Context context;

    public LeftMenuAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        int total = LeftMenuItem.values().length - 1;
        if (BuildConfig.DEBUG && BuildConfig.FLAVOR.equalsIgnoreCase("free")){
            total++;
        }
        return total;
    }

    @Override
    public LeftMenuItem getItem(int position) {
        return LeftMenuItem.values()[position];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LeftMenuHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_left_menu, null);
            holder = new LeftMenuHolder(convertView);
            convertView.setTag(holder);
            convertView.setId(position);
        } else {
            holder = (LeftMenuHolder) convertView.getTag();
        }
        LeftMenuItem leftMenuItem = getItem(position);
        holder.title.setText(leftMenuItem.title);
        holder.icon.setImageResource(leftMenuItem.icon);
        return convertView;
    }

    class LeftMenuHolder {
        TextView     title;
        ImageView   icon;

        public LeftMenuHolder(View row){
            title = (TextView) row.findViewById(R.id.left_menu_title);
            icon = (ImageView) row.findViewById(R.id.left_menu_icon);
        }
    }
}
