package com.tiltcode.tiltcodemanager.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tiltcode.tiltcodemanager.Model.Coupon;
import com.tiltcode.tiltcodemanager.R;

import java.util.ArrayList;

/**
 * Created by JSpiner on 2015. 6. 27..
 */
public class CouponListAdapter extends BaseAdapter {

    //로그에 쓰일 tag
    public static final String TAG = CouponListAdapter.class.getSimpleName();


    public ArrayList<Coupon> arrayList;
    Context context;

    LayoutInflater inflater;

    public CouponListAdapter(Context context, ArrayList<Coupon> arrayList){
        this.arrayList = arrayList;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Log.d(TAG,"init adapter size : "+arrayList.size());
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

        Log.d(TAG,"i : "+i);

        if(view==null) {
            view = inflater.inflate(R.layout.item_coupon_row, null);

            ((TextView)view.findViewById(R.id.tv_couponlist_row)).setText(arrayList.get(i).title);
        }
        return view;
    }
}
