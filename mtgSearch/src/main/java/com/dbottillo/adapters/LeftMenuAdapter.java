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
import com.dbottillo.base.DBFragment;

import java.util.ArrayList;

/**
 * Created by danielebottillo on 14/07/2014.
 */
public class LeftMenuAdapter extends BaseAdapter {

    public enum LeftMenuItem {
        HOME(0, R.string.action_home, R.drawable.left_home, "home"),
        FAVOURITE(1, R.string.action_saved, R.drawable.left_saved, "saved"),
        LIFE_COUNTER(2, R.string.action_life_counter, R.drawable.left_life_counter, "life_counter"),
        FORCE_UPDATE(3, R.string.action_update_database, R.drawable.left_update, null),
        ABOUT(4, R.string.action_about, R.drawable.left_info, null),
        CREATE_DB(5, R.string.action_create_db,  R.drawable.left_info, null);

        private int id;
        private int title;
        private int icon;
        private String tag;

        LeftMenuItem(int id, int title, int icon, String tag){
            this.id = id;
            this.title = title;
            this.icon =  icon;
            this.tag = tag;
        }

        public int getPosition(){
            return id;
        }

        public String getTag() {
            return tag;
        }

        public static LeftMenuItem getItemAtPosition(int position) {
            for (LeftMenuItem leftMenuItem : values()){
                if (leftMenuItem.id == position){
                    return leftMenuItem;
                }
            }
            return null;
        }
    }

    private Context context;
    private ArrayList<LeftMenuItem> items;

    public LeftMenuAdapter(Context context, ArrayList<LeftMenuItem> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
        /*int total = LeftMenuItem.values().length - 1;
        if (BuildConfig.DEBUG && BuildConfig.FLAVOR.equalsIgnoreCase("free")){
            total++;
        }
        return total;*/
    }

    @Override
    public LeftMenuItem getItem(int position) {
        return items.get(position);
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
