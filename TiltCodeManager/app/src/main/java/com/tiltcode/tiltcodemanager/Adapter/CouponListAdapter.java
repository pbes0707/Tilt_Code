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

        Picasso.with(context).load(context.getResources().getText(R.string.API_SERVER)+":40002/couponGetImage?id="
                +arrayList.get(i).id+"."+arrayList.get(i).imageEx).resize(400,400).centerCrop().into(imv);
        //        imageLoader.displayImage(context.getResources().getText(R.string.API_SERVER)+":40002/couponGetImage?id="
        //                +arrayList.get(i).id+"."+arrayList.get(i).imageEx,imv,options);
        tv.setText(arrayList.get(i).title);


        ((LinearLayout)v.findViewById(R.id.layout_coupon_detail)).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mUnfoldableView.unfold(view, mDetailsLayout);

                ImageView couponDetail = ((ImageView) mDetailsLayout.findViewById(R.id.img_coupon_detail));
                couponDetail.setImageDrawable(imv.getDrawable());

                final TextView couponCreate = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_create));
                TextView couponTitle = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_title));
                TextView couponDesc = ((TextView)mDetailsLayout.findViewById(R.id.tv_coupon_detail_desc));

                couponCreate.setText(arrayList.get(i).create);
                couponTitle.setText(arrayList.get(i).title);
                couponDesc.setText(arrayList.get(i).desc);

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


                /*
                ((Button)mDetailsLayout.findViewById(R.id.btn_couponitem_proc)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(arrayList.get(i).type.equals("file")|arrayList.get(i).type.equals("image")) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.getEndPoint().setPort("40002");
                                    retrofit.client.Response response = Util.getHttpSerivce().getFile(Util.getAccessToken().getToken(), arrayList.get(i).id + "." + arrayList.get(i).fileEx);
                                    //                                        byte[] bytes = FileHelper.getBytesFromStream(response.getBody().in());

                                    try {

                                        InputStream stream = (response.getBody().in());

                                        byte[] fileBytes = streamToBytes(stream);

                                        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Downloads/" + arrayList.get(i).id + "." + arrayList.get(i).fileEx);
                                        File filePath = new File(Environment.getExternalStorageDirectory() + "/Downloads/");
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
                                        }
                                    });
                                }
                            }).start();
                        }
                        else if(arrayList.get(i).type.equals("link")){
                            String url = arrayList.get(i).desc;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            context.startActivity(i);
                        }

                    }
                });*/

            }
        });


        return v;
    }
}
