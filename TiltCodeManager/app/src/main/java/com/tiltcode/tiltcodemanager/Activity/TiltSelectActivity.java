package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.tiltcode.tiltcodemanager.Adapter.TiltSelectPagerAdapter;
import com.tiltcode.tiltcodemanager.R;

/**
 * Created by JSpiner on 2015. 7. 4..
 */
public class TiltSelectActivity extends Activity {

    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    void init(){
        pager = (ViewPager)findViewById(R.id.pager_tiltselect);
        TiltSelectPagerAdapter adapter = new TiltSelectPagerAdapter(TiltSelectActivity.this);
        pager.setAdapter(adapter);

    }
}
