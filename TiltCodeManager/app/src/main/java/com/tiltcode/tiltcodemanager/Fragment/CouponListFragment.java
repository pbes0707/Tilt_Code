package com.tiltcode.tiltcodemanager.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import java.util.Collections;
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

    //모든 쿠폰리스트와 선택된 coupon
    public ArrayList<Coupon> couponList;
    public static Coupon coupon;

    ListView listView;
    CouponListAdapter adapter ;

    /*
    ViewPager와 mDetailLayout의 ScrollView와 unFoldableLayout에서
    터치가 겹쳐서 아래와같은 방법으로 intercept해서 해결
     */
    public static View.OnTouchListener interceptTouch = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };

    View mListTouchInterceptor;
    public static UnfoldableView mUnfoldableView;
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

            mListTouchInterceptor = v.findViewById(R.id.touch_interceptor_view);
            mUnfoldableView = (UnfoldableView) v.findViewById(R.id.unfoldable_view);
            mDetailsLayout = (FrameLayout)v.findViewById(R.id.details_layout);

            detailView = inflater.inflate(R.layout.item_foldable_detail,null);
            detailScroll = (ScrollView)detailView.findViewById(R.id.sv_foldable_detail);
            mDetailsLayout.addView(detailView);

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

        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                coupon = couponList.get(i);
                Log.d(TAG,"coupoin id : "+coupon.id);
                ((CouponListActivity)CouponListActivity.context).setPage(2);
            }
        });*/


        //쿠폰 목록 불러옴
        Util.getEndPoint().setPort("40002");
        Util.getHttpSerivce().couponGet(Util.getAccessToken().getToken(),
                new Callback<CouponResult>() {
                    @Override
                    public void success(com.tiltcode.tiltcodemanager.Model.CouponResult couponResult, Response response) {
                        Log.d(TAG, "access success / code : " + couponResult.code);
                        Collections.reverse(couponResult.coupon);
                        if (couponResult.code.equals("1")) { //성공
                            couponList = (ArrayList)couponResult.coupon;
                            Log.d(TAG,"count : "+couponResult.coupon.size());
                            adapter = new CouponListAdapter(context, couponList, mUnfoldableView, detailView);
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



        mListTouchInterceptor.setClickable(false);

        mDetailsLayout.setVisibility(View.INVISIBLE);

        // foldablelayout의 상태가 변할때마다 각각 맞게 처리함
        mUnfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(true);
                mDetailsLayout.setVisibility(View.VISIBLE);
                Log.d(TAG,"unfolding");
            }

            @Override
            public void onUnfolded(UnfoldableView unfoldableView) {
                Log.d(TAG,"unfolded");
                mListTouchInterceptor.setClickable(false);
            }

            @Override
            public void onFoldingBack(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(true);
                Log.d(TAG,"foldingback");
           }

            @Override
            public void onFoldedBack(UnfoldableView unfoldableView) {
                Log.d(TAG,"foldedback");
                mListTouchInterceptor.setClickable(false);
                mDetailsLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFoldProgress(UnfoldableView unfoldableView, float progress) {
                super.onFoldProgress(unfoldableView, progress);
            }
        });


        //mDetailView의 scrollview와 unfoldablelayout터치문제 인터셉트
        detailScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                interceptTouch.onTouch(view,motionEvent);

                touchList.add((int)motionEvent.getY());
                if(touchList.size()>5){
                    touchList.remove(0);
                }
                boolean sw = false;
                if(touchList.size()==5)
                    for(int i=1;i<5;i++){
                        if(touchList.get(i)>touchList.get(0)){
                            sw = true;
                            break;
                        }
                    }
                else{
                    sw=true;
                }
                Log.d(TAG,"y : "+detailScroll.getScrollY() + "touchY : "+motionEvent.getY()+" sw : "+sw);


                if(detailScroll.getScrollY()<10 && sw){
                    if(!lastPressed){
                        motionEvent.setAction(MotionEvent.ACTION_DOWN);
                        Log.d(TAG,"send press down");
                    }
                    interceptTouch.onTouch(view, motionEvent);

                    lastPressed = sw;
                    return true;
                }
                lastPressed = sw;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    lastPressed = false;
                    touchList.clear();
                }

//                Log.d(TAG,"y : "+detailScroll.getScrollY());
//                Log.d(TAG,"y : "+detailScroll.getScrollY());
                return false;
            }
        });

    }

    //터치관련 정보
    boolean lastPressed;
    ArrayList<Integer> touchList = new ArrayList<>();
}
