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
import android.widget.LinearLayout;

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
    private Sensor magnetometer;
    private float[] mGravity = null;
    private float[] mGeomagnetic= null;
    private float sR[] = new float[9];
    private float sI[] = new float[9];
    private float[] mMagnetic;

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

    private float getDirection()
    {

        float[] temp = new float[9];
        float[] R = new float[9];
        //Load rotation matrix into R
        SensorManager.getRotationMatrix(temp, null,
                mGravity, mMagnetic);

        //Remap to camera's point-of-view
        SensorManager.remapCoordinateSystem(temp,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z, R);

        //Return the orientation values
        float[] values = new float[3];
        SensorManager.getOrientation(R, values);

        //Convert to degrees
        for (int i=0; i < values.length; i++) {
            Double degrees = (values[i] * 180) / Math.PI;
            values[i] = degrees.floatValue();
        }

        return values[2];

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(layoutid, container, false);
        init();

        mActivityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        mSensorManager = (SensorManager)getActivity().getSystemService(getActivity().SENSOR_SERVICE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

        if(context == null) context = getActivity();
        tiltView = new TiltCodeView(context);
        ((LinearLayout)v.findViewById(R.id.tiltview_tiltcodefragment)).addView(tiltView);

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
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        switch(event.sensor.getType()) {

            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetic = event.values.clone();
                break;
            default:
                return;
        }
        if(mGravity != null && mMagnetic != null) {
            tiltView.tiltX = getDirection();
//            Log.d("s", "tilt X : " + getDirection());
        }
    }


}
