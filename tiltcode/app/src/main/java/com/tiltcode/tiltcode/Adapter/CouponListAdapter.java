package com.tiltcode.tiltcode.Adapter;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tiltcode.tiltcode.Model.Coupon;
import com.tiltcode.tiltcode.R;

import java.util.List;

/**
 * Created by JSpiner on 2015. 6. 22..
 */
public class CouponListAdapter extends BaseAdapter {

    //로그에 쓰일 tag
    public static final String TAG = CouponListAdapter.class.getSimpleName();

    public List<Coupon> couponList;
    public Context context;
    public UnfoldableView mUnfoldableView;
    public View mDetailsLayout;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;

    public CouponListAdapter(List<Coupon> couponList, Context context, UnfoldableView mUnfoldableView, View mDetailsLayout, ImageLoader imageLoader,DisplayImageOptions options){
        this.couponList = couponList;
        this.context = context;
        this.mUnfoldableView = mUnfoldableView;
        this.mDetailsLayout = mDetailsLayout;
        this.options = options;
        this.imageLoader = imageLoader;
    }


    @Override
    public int getCount() {
        return couponList.size();
    }

    @Override
    public Object getItem(int i) {
        return couponList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View v, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(v==null) {
            v = inflater.inflate(R.layout.item_coupon_row, null);
        }


        final ImageView imv = (ImageView)v.findViewById(R.id.img_coupon_row);
        TextView tv = (TextView)v.findViewById(R.id.tv_coupon_row);

        Log.d(TAG,"i : "+i+" id : "+couponList.get(i).id);

        imageLoader.displayImage(context.getResources().getText(R.string.API_SERVER)+":40002/couponGetImage?id="
                +couponList.get(i).id+"."+couponList.get(i).imageEx,imv,options);
        tv.setText(couponList.get(i).title);


        ((LinearLayout)v.findViewById(R.id.layout_coupon_detail)).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mUnfoldableView.unfold(view, mDetailsLayout);

                ImageView couponDetail = ((ImageView) mDetailsLayout.findViewById(R.id.img_coupon_detail));
                couponDetail.setImageDrawable(imv.getDrawable());

                TextView couponCreate = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_create));
                TextView couponTitle = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_title));
                TextView couponDesc = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_desc));

                couponCreate.setText(couponList.get(i).create);
                couponTitle.setText(couponList.get(i).title);
                couponDesc.setText(couponList.get(i).desc);

            }
        });

        return v;

    }
}
