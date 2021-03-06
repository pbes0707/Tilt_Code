package com.tiltcode.tiltcode.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.foldablelayout.UnfoldableView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tiltcode.tiltcode.Activity.MainActivity;
import com.tiltcode.tiltcode.Adapter.CouponListAdapter;
import com.tiltcode.tiltcode.Adapter.MainPagerAdapter;
import com.tiltcode.tiltcode.Model.Coupon;
import com.tiltcode.tiltcode.Model.CouponResult;
import com.tiltcode.tiltcode.Model.LoginResult;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;
import com.tiltcode.tiltcode.View.ActionActivity;
import com.tiltcode.tiltcode.View.ActionFragmentActivity;
import com.tiltcode.tiltcode.View.BackFragment;
import com.tiltcode.tiltcode.View.DisableViewPager;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by JSpiner on 2015. 6. 17..
 * Contact : jspiner@naver.com
 */
public class CouponListFragment extends BackFragment {

    //로그에 쓰일 tag
    public static final String TAG = CouponListFragment.class.getSimpleName();

    int layoutid;
    Context context;

    public static PullToRefreshListView mListView;
    View mListTouchInterceptor;
    UnfoldableView mUnfoldableView;
    FrameLayout mDetailsLayout;
    View detailView;
    ScrollView detailScroll;

    TextView tv_couponlist_nocoupon;

    List<Coupon> couponList = new ArrayList<>();
    CouponListAdapter couponAdapter;

    View v = null;

    public static View.OnTouchListener interceptTouch = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return false;
        }
    };

    public CouponListFragment() {
        super();
        this.layoutid = R.layout.fragment_couponlist;
        this.context = MainPagerAdapter.context;
    }

    @Override
    public boolean onBackPressed() {
        if (mUnfoldableView != null && (mUnfoldableView.isUnfolded() || mUnfoldableView.isUnfolding())) {
            mUnfoldableView.foldBack();
            return false;
        }
        return true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null) {
            v = inflater.inflate(layoutid, null);

            mListView = (PullToRefreshListView) v.findViewById(R.id.list_view);
            mListTouchInterceptor = v.findViewById(R.id.touch_interceptor_view);
            mUnfoldableView = (UnfoldableView) v.findViewById(R.id.unfoldable_view);
            mDetailsLayout = (FrameLayout)v.findViewById(R.id.details_layout);

            detailView = inflater.inflate(R.layout.item_foldable_detail,null);
            detailScroll = (ScrollView)detailView.findViewById(R.id.sv_foldable_detail);
            mDetailsLayout.addView(detailView);

            tv_couponlist_nocoupon = ((TextView)v.findViewById(R.id.tv_couponlist_nocoupon));

            init();

        }
        return v;
    }

    Handler refreshComplete = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mListView.onRefreshComplete();

        }
    };


    void init() {

        Collections.reverse(couponList);

        couponAdapter = new CouponListAdapter(couponList,context,mUnfoldableView,mDetailsLayout);
        mListView.setAdapter(couponAdapter);

        ((PullToRefreshListView)mListView).setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {

                Util.getEndPoint().setPort("40002");
                Util.getHttpSerivce().couponGet(Util.getAccessToken().getToken()
                        , new Callback<CouponResult>() {
                    @Override
                    public void success(CouponResult couponResult, Response response) {
                        Log.d(TAG, "couponget success / code : " + couponResult.code);
                        refreshComplete.sendEmptyMessageDelayed(0, 1000);

                        if (couponResult.code.equals("1")) { //성공

                            if (couponResult.coupon != null) {

                                Collections.reverse(couponResult.coupon);
                                Log.d(TAG, "count : " + couponResult.coupon.size());

                                couponAdapter.couponList = couponResult.coupon;
                                couponAdapter.notifyDataSetChanged();

                                if (couponResult.coupon.size() == 0) {
                                    tv_couponlist_nocoupon.setVisibility(View.VISIBLE);
                                } else {
                                    tv_couponlist_nocoupon.setVisibility(View.GONE);
                                }

                            } else {
                                tv_couponlist_nocoupon.setVisibility(View.GONE);
                            }

                        } else if (couponResult.code.equals("-1")) { //누락된게있음
                            Toast.makeText(context, getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                        } else if (couponResult.code.equals("-2")) { //유효하지않은 토큰
                            Toast.makeText(context, getResources().getText(R.string.message_not_allow_permission), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "login failure : " + error.getMessage());
                        Toast.makeText(context, getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();

                        refreshComplete.sendEmptyMessageDelayed(0, 1000);

                    }
                });
            }
        });

        mListTouchInterceptor.setClickable(false);

        mDetailsLayout.setVisibility(View.INVISIBLE);

        final View tab = getActivity().findViewById(R.id.layout_main_tabview);

        mUnfoldableView.setOnFoldingListener(new UnfoldableView.SimpleFoldingListener() {
            @Override
            public void onUnfolding(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(true);
                mDetailsLayout.setVisibility(View.VISIBLE);
                Log.d(TAG,"unfolding");

                tab.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
                tab.setVisibility(View.GONE);

                        ((ActionFragmentActivity) context).setEnableBack(true);
            }

            @Override
            public void onUnfolded(UnfoldableView unfoldableView) {
                Log.d(TAG,"unfolded");
                mListTouchInterceptor.setClickable(false);
                ((DisableViewPager)MainActivity.mPager).enableTouch=false;
            }

            @Override
            public void onFoldingBack(UnfoldableView unfoldableView) {
                mListTouchInterceptor.setClickable(true);
                Log.d(TAG,"foldingback");

                tab.setAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
                tab.setVisibility(View.VISIBLE);
                ((ActionFragmentActivity)context).setEnableBack(false);
            }

            @Override
            public void onFoldedBack(UnfoldableView unfoldableView) {
                Log.d(TAG,"foldedback");
                mListTouchInterceptor.setClickable(false);
                mDetailsLayout.setVisibility(View.INVISIBLE);
                ((DisableViewPager)MainActivity.mPager).enableTouch=true;
            }

            @Override
            public void onFoldProgress(UnfoldableView unfoldableView, float progress) {
                super.onFoldProgress(unfoldableView, progress);
//                Log.d(TAG,"progress : "+progress + " / height : "+(int) ((float)layout_main_tab_height * progress) + "rheight : "+MainActivity.layout_main_tab.getHeight());

//                MainActivity.layout_main_tab.animate().translationY(progress*layout_main_tab_height).start();
//                MainActivity.layout_main_tab.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int) ((float)layout_main_tab_height * (1-progress))));
            }
        });


        Util.getEndPoint().setPort("40002");
        Util.getHttpSerivce().couponGet(Util.getAccessToken().getToken()
                , new Callback<CouponResult>() {
            @Override
            public void success(CouponResult couponResult, Response response) {
                Log.d(TAG,"couponget success / code : "+couponResult.code);
                if (couponResult.code.equals("1")) { //성공

                    if(couponResult.coupon!=null) {
                        Log.d(TAG, "count : " + couponResult.coupon.size());

                        Collections.reverse(couponResult.coupon);

                        couponAdapter.couponList = couponResult.coupon;
                        couponAdapter.notifyDataSetChanged();

                        if(couponResult.coupon.size()==0){
                            tv_couponlist_nocoupon.setVisibility(View.VISIBLE);
                        }
                        else{
                            tv_couponlist_nocoupon.setVisibility(View.GONE);
                        }

                    }
                    else{
                        tv_couponlist_nocoupon.setVisibility(View.GONE);
                    }

                } else if (couponResult.code.equals("-1")) { //누락된게있음
                    Toast.makeText(context,getResources().getText(R.string.message_not_enough_data),Toast.LENGTH_LONG).show();
                } else if (couponResult.code.equals("-2")) { //유효하지않은 토큰
                    Toast.makeText(context,getResources().getText(R.string.message_not_allow_permission),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG,"login failure : "+error.getMessage());
                Toast.makeText(context,getResources().getText(R.string.message_network_error),Toast.LENGTH_LONG).show();

            }
        });


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

    boolean lastPressed;
    ArrayList<Integer> touchList = new ArrayList<>();

}
