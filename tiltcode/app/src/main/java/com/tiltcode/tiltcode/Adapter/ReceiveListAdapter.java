package com.tiltcode.tiltcode.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tiltcode.tiltcode.Activity.CouponReceiveActivity;
import com.tiltcode.tiltcode.Model.Coupon;
import com.tiltcode.tiltcode.R;

import java.util.ArrayList;

/**
 * Created by JSpiner on 2015. 7. 8..
 */
public class ReceiveListAdapter extends BaseAdapter {


    //로그에 쓰일 tag
    public static final String TAG = ReceiveListAdapter.class.getSimpleName();


    ArrayList<Coupon> arrayList;
    public ArrayList<CheckBox> checkList;
    Context context;

    LayoutInflater inflater;



    public ReceiveListAdapter(Context context, ArrayList<Coupon> arrayList){
        this.arrayList = arrayList;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        checkList = new ArrayList<>();

        init();
    }


    void init(){

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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if(view==null) {
            view = inflater.inflate(R.layout.item_couponreceive_row, null);
        }

        ((TextView)view.findViewById(R.id.tv_couponreceive_row)).setText(arrayList.get(i).title);
            ImageView imv = (ImageView)view.findViewById(R.id.img_couponreceive_row);


        Log.d(TAG,"url : "+context.getResources().getText(R.string.API_SERVER)+":40002/couponGetImage?id="
                +arrayList.get(i).id+"."+arrayList.get(i).imageEx);

            Picasso.with(context).load(context.getResources().getText(R.string.API_SERVER)+":40002/couponGetImage?id="
                    +arrayList.get(i).id+"."+arrayList.get(i).imageEx).resize(400,400).centerCrop().into(imv);
//            imageLoader.displayImage(context.getResources().getText(R.string.API_SERVER)+":40002/couponGetImage?id="
 //                   +arrayList.get(i).id+"."+arrayList.get(i).imageEx,imv,options);
  //          if(checkList.size()<=i){
            ((CheckBox)view.findViewById(R.id.cb_couponreceive_row)).setTag(i);
                checkList.add((CheckBox)view.findViewById(R.id.cb_couponreceive_row));

//            }
            ((CheckBox)view.findViewById(R.id.cb_couponreceive_row)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CouponReceiveActivity.selectedIndex = i;
                    Log.d(TAG, "selectedIndex : " + i);
                    for (int j = 0; j < checkList.size(); j++) {
                        if ((int)checkList.get(j).getTag()==i) continue;
                        Log.d(TAG, "unchecked id : " + j);
                        checkList.get(j).setChecked(false);

                    }
                }
            });

        return view;
    }



}
