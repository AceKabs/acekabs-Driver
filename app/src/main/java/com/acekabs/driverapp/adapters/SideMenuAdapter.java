package com.acekabs.driverapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.acekabs.driverapp.R;
import com.acekabs.driverapp.pojo.MenuData;

import java.util.ArrayList;

/**
 * Created by ankit.jain on 5/4/2017.
 */

public class SideMenuAdapter extends BaseAdapter {
    private ArrayList<MenuData> menuDatas;
    private Context mContext;
    private LayoutInflater inflater;

    public SideMenuAdapter(ArrayList<MenuData> menuDatas, Context mContext) {
        this.menuDatas = menuDatas;
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return menuDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return menuDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            // inflate the layout
            convertView = inflater.inflate(R.layout.row_menu, parent, false);
            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
            convertView.setTag(viewHolder);
        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MenuData data = menuDatas.get(position);
        viewHolder.tvTitle.setText(data.getMenuTitle());
        viewHolder.imgIcon.setImageResource(data.getMenuIcon());
        return convertView;
    }

    static class ViewHolder
    {
        ImageView imgIcon;
        TextView tvTitle;
    }
}
