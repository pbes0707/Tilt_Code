package com.tiltcode.tiltcode.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tiltcode.tiltcode.Adapter.ReceiveListAdapter;
import com.tiltcode.tiltcode.Fragment.CouponListFragment;
import com.tiltcode.tiltcode.Model.Coupon;
import com.tiltcode.tiltcode.Model.LoginResult;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Service.TiltService;
import com.tiltcode.tiltcode.Util;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 7. 4..
 */
public class CouponReceiveActivity extends Activity {

    //로그에 쓰일 tag
    public static final String TAG = CouponReceiveActivity.class.getSimpleName();

//    RadioGroup radioGroup;
    LayoutInflater inflater;
    ArrayList<Coupon> couponList;
    ListView listview;

    public static int selectedIndex = 0;

    ReceiveListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_couponreceive);

        init();
    }

    void init(){
        this.couponList = (ArrayList)TiltService.couponList;

//        radioGroup = (RadioGroup)findViewById(R.id.radiogroup_couponreceive);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adapter = new ReceiveListAdapter(getBaseContext(), couponList);
        listview = (ListView)findViewById(R.id.lv_couponreceive);
        listview.setAdapter(adapter);
        for(int i=0;i<couponList.size();i++){
//            RadioButton v = (RadioButton)inflater.inflate(R.layout.item_radio_row,null);
//            v.setId(i);
 //           v.setText(couponList.get(i).title);
//            ((RadioButton)v.findViewById(R.id.radio_coupon_row)).set
 //           radioGroup.addView(v);

        }
/*
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                selectedIndex = i;
            }
        });*/

        ((Button)findViewById(R.id.btn_couponreceive_proc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.getEndPoint().setPort("40002");
                Util.getHttpSerivce().couponAdd(Util.getAccessToken().getToken(),
                        couponList.get(selectedIndex).id,
                        new Callback<LoginResult>() {
                            @Override
                            public void success(LoginResult loginResult, Response response) {
                                if (loginResult.code.equals("1")) { //성공
                                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_success_receive),Toast.LENGTH_LONG).show();
                                    if(CouponListFragment.mListView != null){
                                        CouponListFragment.mListView.setRefreshing();
                                    }
                                    finish();
                                } else if (loginResult.code.equals("-1")) { //누락된게있음
                                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_not_enough_data),Toast.LENGTH_LONG).show();
                                } else if (loginResult.code.equals("-2")) { //알수없는 쿠폰 id
                                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_unkown_coupon_id),Toast.LENGTH_LONG).show();
                                } else if (loginResult.code.equals("-3")) { //비활성화된 쿠폰
                                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_deactivated_coupon),Toast.LENGTH_LONG).show();
                                } else if (loginResult.code.equals("-4")) { //유효하지 않은 세션
                                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_session_invalid),Toast.LENGTH_LONG).show();
                                } else if (loginResult.code.equals("-5")) { //발급자가 존재하지 않음
                                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_unkown_coupon_create),Toast.LENGTH_LONG).show();
                                } else if (loginResult.code.equals("-6")) { //발급자의 포인트가 부족
                                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_no_enough_point),Toast.LENGTH_LONG).show();
                                } else if (loginResult.code.equals("-7")) { //이미 가지고 있음
                                    Toast.makeText(getBaseContext(),getResources().getText(R.string.message_already_have_coupon),Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                                Log.d(TAG, "onError" + error.getMessage());

                                Toast.makeText(getBaseContext(), getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }
}
