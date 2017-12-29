package com.abby.wuziqi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.abby.wuziqi.R;
import com.abby.wuziqi.bean.BlueTooth;
import com.abby.wuziqi.game.BlueConnectActivity;

import java.util.List;

/**
 * Created by keybo on 2017/12/26 0026.
 */

public class BlueToothDevicesAdapter extends BaseAdapter {
    private List<BlueTooth> blueTooths;
    private Context context;
    private LayoutInflater inflater;

    public BlueToothDevicesAdapter(List<BlueTooth> blueTooths,Context context){
        this.blueTooths = blueTooths;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public BlueToothDevicesAdapter(List<BlueTooth> blueTooths, BlueConnectActivity blueConnectActivity) {
    }

    @Override
    public int getCount() {
        return blueTooths.size();
    }

    @Override
    public Object getItem(int position) {
        return blueTooths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null){
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_bluetooth,null);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.adress = view.findViewById(R.id.adress);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(blueTooths.get(position).getName());
        viewHolder.adress.setText(blueTooths.get(position).getAddress());

        return view;
    }

    public void setDevices(List<BlueTooth> bluetooths) {

        bluetooths = bluetooths;
    }

    class ViewHolder {
        TextView name;
        TextView adress;
    }
}
