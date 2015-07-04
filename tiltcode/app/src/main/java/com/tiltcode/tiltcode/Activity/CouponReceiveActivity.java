package com.tiltcode.tiltcode.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tiltcode.tiltcode.R;

/**
 * Created by JSpiner on 2015. 7. 4..
 */
public class CouponReceiveActivity extends Activity {

    RadioGroup radioGroup;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_couponreceive);

        init();
    }

    void init(){

        radioGroup = (RadioGroup)findViewById(R.id.radiogroup_couponreceive);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i=0;i<5;i++){
            RadioButton v = (RadioButton)inflater.inflate(R.layout.item_radio_row,null);
            v.setId(i);
//            ((RadioButton)v.findViewById(R.id.radio_coupon_row)).set
            radioGroup.addView(v);

        }

    }
}
