package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.tiltcode.tiltcodemanager.Adapter.TiltSelectPagerAdapter;
import com.tiltcode.tiltcodemanager.R;

/**
 * Created by JSpiner on 2015. 7. 4..
 */
public class TiltSelectActivity extends Activity {

    ViewPager pager;

    int nowPage=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiltselect);

        init();
    }

    void init(){
        //pager 셋팅
        pager = (ViewPager)findViewById(R.id.pager_tiltselect);
        TiltSelectPagerAdapter adapter = new TiltSelectPagerAdapter(TiltSelectActivity.this);
        pager.setAdapter(adapter);


        //선택된 tilt값을 result에 넣어서 리턴함
        ((LinearLayout)findViewById(R.id.layout_tiltselect_proc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("tiltValue", (nowPage+1));

                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                nowPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
