package com.tiltcode.tiltcode.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.squareup.picasso.Picasso;
import com.tiltcode.tiltcode.Model.Coupon;
import com.tiltcode.tiltcode.Model.LoginResult;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.client.*;
import retrofit.mime.TypedByteArray;

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

    public CouponListAdapter(List<Coupon> couponList, Context context, UnfoldableView mUnfoldableView, View mDetailsLayout){
        this.couponList = couponList;
        this.context = context;
        this.mUnfoldableView = mUnfoldableView;
        this.mDetailsLayout = mDetailsLayout;
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

        Picasso.with(context).load(context.getResources().getText(R.string.API_SERVER)+":40002/couponGetImage?id="
                +couponList.get(i).id+"."+couponList.get(i).imageEx).resize(400,400).centerCrop().into(imv);
//        imageLoader.displayImage(context.getResources().getText(R.string.API_SERVER)+":40002/couponGetImage?id="
//                +couponList.get(i).id+"."+couponList.get(i).imageEx,imv,options);
        tv.setText(couponList.get(i).title);


        ((LinearLayout)v.findViewById(R.id.layout_coupon_detail)).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mUnfoldableView.unfold(view, mDetailsLayout);

                ImageView couponDetail = ((ImageView) mDetailsLayout.findViewById(R.id.img_coupon_detail));
                couponDetail.setImageDrawable(imv.getDrawable());

                final TextView couponCreate = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_create));
                TextView couponTitle = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_title));
                TextView couponDesc = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_desc));

                couponCreate.setText(couponList.get(i).create);
                couponTitle.setText(couponList.get(i).title);
                couponDesc.setText(couponList.get(i).desc);

                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                Address address;
                String result = null;
                List<Address> list = null;
                try {
                    Log.d(TAG, "lat : " + couponList.get(i).lat+" lng : "+couponList.get(i).lng);
                    list = geocoder.getFromLocation(Double.valueOf(couponList.get(i).lat),Double.valueOf(couponList.get(i).lng), 1);
                    address = list.get(0);
                    result = address.getAddressLine(0) + ", " + address.getLocality();

                    ((TextView) mDetailsLayout.findViewById(R.id.tv_coupon_detail_location)).setText(result);
                    Log.d(TAG, "location : " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "error : " + e.getMessage());
                }


                File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Download/" + couponList.get(i).tilt + "." + couponList.get(i).fileEx);
                if(pdfFile.exists()){
                    ((Button)mDetailsLayout.findViewById(R.id.btn_couponitem_proc)).setText("열기");
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(pdfFile));
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        context.startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(context,"파일을 열수 없습니다. "+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }

                ((Button)mDetailsLayout.findViewById(R.id.btn_couponitem_proc)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(((Button)view).getText().toString().equals("열기")){

                        }
                        else {
                            if (couponList.get(i).type.equals("file") | couponList.get(i).type.equals("image")) {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Util.getEndPoint().setPort("40002");
                                        retrofit.client.Response response = Util.getHttpSerivce().getFile(Util.getAccessToken().getToken(), couponList.get(i).id + "." + couponList.get(i).fileEx);
//                                        byte[] bytes = FileHelper.getBytesFromStream(response.getBody().in());

                                        try {

                                            InputStream stream = (response.getBody().in());

                                            byte[] fileBytes = streamToBytes(stream);

                                            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Download/" + couponList.get(i).tilt + "." + couponList.get(i).fileEx);
                                            File filePath = new File(Environment.getExternalStorageDirectory() + "/Download/");
                                            filePath.mkdir();
                                            Log.d(TAG, "file : " + pdfFile.getAbsolutePath() + " name : " + pdfFile.getName() + " size : " + fileBytes.length);

                                            FileOutputStream output = null;
                                            output = new FileOutputStream(pdfFile);
                                            output.write(fileBytes);
                                            output.flush();
                                            output.close();
//                                            org.apache.commons.io.IOUtils.write(fileBytes, output);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e(TAG, "error : " + e.getMessage());
                                            ((Activity) context).runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(context, context.getResources().getText(R.string.message_download_coupon_fail), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            return;
                                        }
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                Toast.makeText(context, context.getResources().getText(R.string.message_download_coupon_success), Toast.LENGTH_LONG).show();

                                                ((Button)mDetailsLayout.findViewById(R.id.btn_couponitem_proc)).setText("열기");
                                            }
                                        });
                                    }
                                }).start();
                            } else if (couponList.get(i).type.equals("link")) {
                                String url = couponList.get(i).desc;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                context.startActivity(i);
                            }
                        }
                    }
                });

            }
        });


        return v;

    }

    byte[] streamToBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (stream != null) {
            byte[] buf = new byte[1024];
            int r;
            while ((r = stream.read(buf)) != -1) {
                baos.write(buf, 0, r);
            }
        }
        return baos.toByteArray();
    }

}
