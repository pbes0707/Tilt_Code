package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

/**
 * Created by JSpiner on 2015. 7. 4..
 */
public class TiltSelectActivity extends Activity {

    private SensorManager manager;
    private SensorEventListener listener;


    private static float[][] Arr_Accel = {
            {0f, 9.8f, 0f},
            {6.9f, 6.8f, 0f},
            {9.8f, 0f, 0f},
            {6.8f, -6.8f, 0f},
            {0f, -9.8f, 0f}, // 5
            {-6.8f, -6.8f, 0f},
            {-9.8f, 0f, 0f},
            {-6.8f, 6.8f, 0f},
            {0f, 6.8f, 6.8f}, // 9
            {0f, 0f, 9.8f},
            {0f, -6.8f, 6.8f},
            //{0f, -9.8f, 0f}, // 12
            {0f, -6.8f, -6.8f}, // Fixed 12  original 13
            {0f, 0f, -9.8f},
            {0f, 6.8f, -6.8f}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    void init(){

        manager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        listener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor arg0, int arg1) {
            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                Sensor sensor = event.sensor;
                if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                }
                else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {

                }
            }
        }

        manager.registerListener(listener, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(listener, manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
    }
}
