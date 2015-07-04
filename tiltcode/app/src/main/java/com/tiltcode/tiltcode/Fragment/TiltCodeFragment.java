package com.tiltcode.tiltcode.Fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiltcode.tiltcode.Adapter.MainPagerAdapter;
import com.tiltcode.tiltcode.Model.AccelData;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.View.BackFragment;
import com.tiltcode.tiltcode.View.TiltCodeView;


/**
 * Created by JSpiner on 2015. 6. 15..
 * Contact : jspiner@naver.com
 */
public class TiltCodeFragment extends BackFragment implements SensorEventListener {


    private AccelData accelData = null;
    private ActivityManager mActivityManager;
    private SensorManager mSensorManager;
    private Sensor accelerometerSensor;
    //로그에 쓰일 tag
    public static final String TAG = TiltCodeFragment.class.getSimpleName();

    int layoutid;
    Context context;

    TiltCodeView tiltView;

    public TiltCodeFragment() {
        super();
        this.layoutid = R.layout.fragment_tiltcode;
        this.context = MainPagerAdapter.context;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(layoutid, container, false);
        init();

        mActivityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        mSensorManager = (SensorManager)getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        tiltView = (TiltCodeView)v.findViewById(R.id.tiltview_tiltcodefragment);

        return v;
    }

    void init() {
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }
    @Override
    public void onResume()
    {
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        switch(event.sensor.getType())
        {
            // 가속도 센서일때
            case Sensor.TYPE_ACCELEROMETER:
            {
                float[] values = event.values;
                accelData = new AccelData(
                        Math.round(values[0] * 100d) / 100d,
                        Math.round(values[1] * 100d) / 100d,
                        Math.round(values[2] * 100d) / 100d);

                Log.d(TAG,"x : "+accelData.x+" y : "+accelData.y+" z :"+accelData.z);
                tiltView.tiltX = (float)accelData.x;
                break;
            }
        }
    }


}
