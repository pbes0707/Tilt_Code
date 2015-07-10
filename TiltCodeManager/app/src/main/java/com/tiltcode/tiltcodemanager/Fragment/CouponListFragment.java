package com.tiltcode.tiltcodemanager.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.tiltcode.tiltcodemanager.Activity.CouponListActivity;
import com.tiltcode.tiltcodemanager.Activity.MainActivity;
import com.tiltcode.tiltcodemanager.Activity.SignupActivity;
import com.tiltcode.tiltcodemanager.Adapter.CouponListAdapter;
import com.tiltcode.tiltcodemanager.Model.Coupon;
import com.tiltcode.tiltcodemanager.Model.CouponResult;
import com.tiltcode.tiltcodemanager.Model.LoginResult;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 6. 27..
 */
public class CouponListFragment extends Fragment {

    //로그에 쓰일 tag
    public static final String TAG = CouponListFragment.class.getSimpleName();

    int layoutid;
    Context context;

    ProgressDialog dialog;

    public ArrayList<Coupon> couponList;
    public static Coupon coupon;

    ListView listView;
    CouponListAdapter adapter ;


    View mListTouchInterceptor;
    UnfoldableView mUnfoldableView;
    FrameLayout mDetailsLayout;
    View detailView;
    ScrollView detailScroll;

    public CouponListFragment() {
        super();
        this.layoutid = R.layout.fragment_couponlist;
        this.context = CouponListActivity.context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = null;

        if (v == null) {
            v = inflater.inflate(layoutid, null);

            listView = (ListView)v.findViewById(R.id.lv_couponlist);

            init();

        }
        return v;
    }

    void init() {

        Log.d(TAG,"couponlistfragment");


        dialog = new ProgressDialog(context);
        dialog.setTitle("로드중");
        dialog.setMessage("데이터를 불러오는중입니다..");
        dialog.show();

        couponList = new ArrayList<Coupon>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                coupon = couponList.get(i);
                Log.d(TAG,"coupoin id : "+coupon.id);
                ((CouponListActivity)CouponListActivity.context).setPage(2);
            }
        });


        Util.getEndPoint().setPort("40002");
        Util.getHttpSerivce().couponGet(Util.getAccessToken().getToken(),
                new Callback<CouponResult>() {
                    @Override
                    public void success(com.tiltcode.tiltcodemanager.Model.CouponResult couponResult, Response response) {
                        Log.d(TAG, "access success / code : " + couponResult.code);
                        if (couponResult.code.equals("1")) { //성공
                            couponList = (ArrayList)couponResult.coupon;
                            Log.d(TAG,"count : "+couponResult.coupon.size());
                            adapter = new CouponListAdapter(context, couponList);
//                            adapter.arrayList = couponList;
//                            adapter.notifyDataSetChanged();
                            listView.setAdapter(adapter);
                        } else if (couponResult.code.equals("-1")) { //누락된게있음
                            Toast.makeText(context, getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                        } else if (couponResult.code.equals("-2")) { //세션이 유효하지않음
                            Toast.makeText(context, getResources().getText(R.string.message_session_invalid), Toast.LENGTH_LONG).show();
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "login failure : " + error.getMessage());
                        Toast.makeText(context, getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }
                });

    }
}
