package com.tiltcode.tiltcode.Service;

/**
 * Created by Secret on 2015. 6. 16..
 */
import android.app.ActivityManager;
import android.app.Service;
import android.content.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.List;

/**
 * User: Secret
 * Date: 2015. 6. 18.
 * Time: 오후 6:02
 */
public class TiltService extends Service implements SensorEventListener {

    private final String LOG_NAME = TiltService.class.getSimpleName();

    public static Thread mThread;

    private ComponentName recentComponentName;
    private ActivityManager mActivityManager;
    private SensorManager mSensorManager;
    private Sensor gyroSensor;
    private Sensor accelerometerSensor;

    private boolean serviceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();

        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // 자이로 센서 리스너 오브젝트를 등록
        mSensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // 가속도 센서 리스너 오브젝트를 등록
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);


        serviceRunning = true;
    }
    @Override
    public void onDestroy() {
        serviceRunning = false;
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (serviceRunning) {
                        SystemClock.sleep(2000);
                    }
                }
            });

            mThread.start();
        } else if (mThread.isAlive() == false) {
            mThread.start();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
                float x = values[0];
                float y = values[1];
                float z = values[2];
                Log.d("sensor","Accel X : " + Math.round(x*100d) / 100d +
                        " Y : " + Math.round(y*100d) / 100d +
                        " Z : " + Math.round(z*100d) / 100d);
                break;
            }
            //자이로스코프 센서일때
            /*case Sensor.TYPE_GYROSCOPE:
            {
                float[] values = event.values;
                float x = values[0] * 1000;
                float y = values[1] * 1000;
                float z = values[2] * 1000;
                Log.d("sensor","Gyro X : " + Math.round(x*100d) / 100d +
                        " Y : " + Math.round(y*100d) / 100d +
                        " Z : " + Math.round(z*100d) / 100d);
                break;
            }*/
        }
    }


}