package com.tiltcode.tiltcode.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tiltcode.tiltcode.R;

import java.util.ArrayList;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class SettingListAdapter extends BaseAdapter {

    ArrayList<String> arrayList;
    Context context;

    LayoutInflater inflater;

    public SettingListAdapter(Context context, ArrayList<String> arrayList){
        this.arrayList = arrayList;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view==null) {
            view = inflater.inflate(R.layout.item_setting_row, null);
            ((TextView)view.findViewById(R.id.tv_setting_row)).setText(arrayList.get(i));
        }
        return view;
    }
}
