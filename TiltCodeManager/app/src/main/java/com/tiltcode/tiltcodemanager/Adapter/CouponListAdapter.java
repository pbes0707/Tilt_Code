package com.tiltcode.tiltcodemanager.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.squareup.picasso.Picasso;
import com.tiltcode.tiltcodemanager.Activity.CouponListActivity;
import com.tiltcode.tiltcodemanager.Fragment.CouponListFragment;
import com.tiltcode.tiltcodemanager.Model.Coupon;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by JSpiner on 2015. 6. 27..
 */
public class CouponListAdapter extends BaseAdapter {

    //로그에 쓰일 tag
    public static final String TAG = CouponListAdapter.class.getSimpleName();


    public ArrayList<Coupon> arrayList;
    Context context;

    LayoutInflater inflater;


    public UnfoldableView mUnfoldableView;
    public View mDetailsLayout;

    public CouponListAdapter(Context context, ArrayList<Coupon> arrayList, UnfoldableView mUnfoldableView, View mDetailsLayout){
        this.arrayList = arrayList;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mUnfoldableView = mUnfoldableView;
        this.mDetailsLayout = mDetailsLayout;

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
    public View getView(final int i, View v, ViewGroup viewGroup) {

        Log.d(TAG,"i : "+i);

        if(v==null) {
            v = inflater.inflate(R.layout.item_coupon_row, null);
        }

        final ImageView imv = (ImageView)v.findViewById(R.id.img_coupon_row);
        TextView tv = (TextView)v.findViewById(R.id.tv_coupon_row);

        Log.d(TAG,"i : "+i+" id : "+arrayList.get(i).id);


        //이미지를 불러옴 Picasso
        Picasso.with(context).load(context.getResources().getText(R.string.API_SERVER)+":40002/couponGetImage?id="
                +arrayList.get(i).id+"."+arrayList.get(i).imageEx).resize(400,400).centerCrop().into(imv);
        //        imageLoader.displayImage(context.getResources().getText(R.string.API_SERVER)+":40002/couponGetImage?id="
        //                +arrayList.get(i).id+"."+arrayList.get(i).imageEx,imv,options);
        tv.setText(arrayList.get(i).title);


        //쿠폰 선택시 foldableview 설정
        ((LinearLayout)v.findViewById(R.id.layout_coupon_detail)).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mUnfoldableView.unfold(view, mDetailsLayout);

                ImageView couponDetail = ((ImageView) mDetailsLayout.findViewById(R.id.img_coupon_detail));
                couponDetail.setImageDrawable(imv.getDrawable());

                final TextView couponCreate = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_create));
                TextView couponTitle = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_title));
                TextView couponDesc = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_desc));

                couponCreate.setText(Util.decrypt(arrayList.get(i).create));
                couponTitle.setText(arrayList.get(i).title);
                couponDesc.setText(arrayList.get(i).desc);

                //gps 좌표를 기준으로 주소값을 가져옴
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                Address address;
                String result = null;
                List<Address> list = null;
                try {
                    Log.d(TAG, "lat : " + arrayList.get(i).lat+" lng : "+arrayList.get(i).lng);
                    list = geocoder.getFromLocation(Double.valueOf(arrayList.get(i).lat),Double.valueOf(arrayList.get(i).lng), 1);
                    address = list.get(0);
                    result = address.getAddressLine(0) + ", " + address.getLocality();

                    ((TextView) mDetailsLayout.findViewById(R.id.tv_coupon_detail_location)).setText(result);
                    Log.d(TAG, "location : " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "error : " + e.getMessage());
                }


                //수정과 통계 선택시 해당 페이지로 이동
                ((Button)mDetailsLayout.findViewById(R.id.btn_couponitem_anaylize)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CouponListFragment.coupon = arrayList.get(i);
                        ((CouponListActivity) CouponListActivity.context).setPage(2);
                    }
                });

                ((Button)mDetailsLayout.findViewById(R.id.btn_couponitem_edit)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CouponListFragment.coupon = arrayList.get(i);
                        ((CouponListActivity) CouponListActivity.context).setPage(3);
                    }
                });

            }
        });


        return v;
    }
}
