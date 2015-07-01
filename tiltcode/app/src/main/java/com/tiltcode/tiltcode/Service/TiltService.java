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

import com.tiltcode.tiltcode.Activity.SplashActivity;
import com.tiltcode.tiltcode.Model.AccelData;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Secret
 * Date: 2015. 6. 18.
 * Time: 오후 6:02
 */
public class TiltService extends Service implements SensorEventListener {

    private final String LOG_NAME = TiltService.class.getSimpleName();

    public static Thread mThread;
    private LinkedList<AccelData> list;
    private static int dt = 0, count = 0;
    private static AccelData prev = null, now = null, avg = null;
    private float TOLERANCE_VALUE = 2.f;
    private float SEARCH_VALUE = 2.5f;
    private float SENSITIVE_SEARCH_VALUE = 1.3f;
    private int RECOGNIZE = 3000;
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

    private ComponentName recentComponentName;
    private ActivityManager mActivityManager;
    private SensorManager mSensorManager;
    private Sensor gyroSensor;
    private Sensor accelerometerSensor;

    private boolean serviceRunning = false;

    @Override
    public void onCreate()
    {
        super.onCreate();

        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // 자이로 센서 리스너 오브젝트를 등록
        mSensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // 가속도 센서 리스너 오브젝트를 등록
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        list = new LinkedList<AccelData>();
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
                        SystemClock.sleep(30);
                        dt += 30;
                        if(dt > RECOGNIZE)
                        {
                            for(int i = 0 ; i<Arr_Accel.length ; i++)
                            {
                                if( (Arr_Accel[i][0] - SENSITIVE_SEARCH_VALUE < avg.x && Arr_Accel[i][0] + SENSITIVE_SEARCH_VALUE > avg.x ) &&
                                        (Arr_Accel[i][1] - SENSITIVE_SEARCH_VALUE < avg.y && Arr_Accel[i][1] + SENSITIVE_SEARCH_VALUE > avg.y ) &&
                                        (Arr_Accel[i][2] - SENSITIVE_SEARCH_VALUE < avg.z && Arr_Accel[i][2] + SENSITIVE_SEARCH_VALUE > avg.z ) )
                                {
                                    Log.d("sensitive sensor", "Tilt : " + String.valueOf(i + 1));
                                    dt = 0;
                                    //////////////정밀 검사 부분/////////
                                }
                                else if( (Arr_Accel[i][0] - SEARCH_VALUE < avg.x && Arr_Accel[i][0] + SEARCH_VALUE > avg.x ) &&
                                        (Arr_Accel[i][1] - SEARCH_VALUE < avg.y && Arr_Accel[i][1] + SEARCH_VALUE > avg.y ) &&
                                        (Arr_Accel[i][2] - SEARCH_VALUE < avg.z && Arr_Accel[i][2] + SEARCH_VALUE > avg.z ) )
                                {
                                    Log.d("sensor", "Tilt : " + String.valueOf(i + 1));
                                    dt = 0;
                                   /////////////////일반 검사 부분 //////////////

                                }
                            }
                        }
                       /* boolean flag = false;

                        if(list.size() > 29) {
                            double[][] values = new double[list.size()][];
                            for (int i = 0; i < list.size(); i++) {
                                values[i] = new double[3];
                                values[i][0] = list.get(i).x;
                                values[i][1] = list.get(i).y;
                                values[i][2] = list.get(i).z;
                            }

                            double deviX = Float.parseFloat(Double.toString(standardDeviation(values[0], 0)));
                            double deviY = Float.parseFloat(Double.toString(standardDeviation(values[1], 0)));
                            double deviZ = Float.parseFloat(Double.toString(standardDeviation(values[2], 0)));


                            Log.d("sensor", "Deviation X : " + deviX +
                                    " Y : " + deviY +
                                    " Z : " + deviZ);

                            if (flag) {
                                Intent i = new Intent(TiltService.this, SplashActivity.class);
                                startActivity(i);
                                flag = false;
                            }
                        }*/
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
                AccelData v = new AccelData(
                        Math.round(values[0] * 100d) / 100d,
                        Math.round(values[1] * 100d) / 100d,
                        Math.round(values[2] * 100d) / 100d);

                if(prev == null)
                {
                    avg = prev = now = v;
                    count = 1;
                }
                else
                {
                    if( !((avg.x - TOLERANCE_VALUE < v.x && avg.x + TOLERANCE_VALUE > v.x)
                            && (avg.y - TOLERANCE_VALUE < v.y && avg.y + TOLERANCE_VALUE > v.y)
                            && (avg.z - TOLERANCE_VALUE < v.z && avg.z + TOLERANCE_VALUE > v.z)))
                    {
                        dt = 0;
                        avg = prev = v;
                        count = 1;
                    }
                    else
                    {
                        count++;
                        prev.x += v.x;
                        prev.y += v.y;
                        prev.z += v.z;
                        avg.x = prev.x/count;
                        avg.y = prev.y/count;
                        avg.z = prev.z/count;
                    }
                    now = v;
                }
                /* Log.d("sensor","Accel X : " + Math.round(v.x*100d) / 100d +
                        " Y : " + Math.round(v.y*100d) / 100d +
                        " Z : " + Math.round(v.z*100d) / 100d);*/
                /*list.addFirst(v);
                if(list.size() > 30)
                    list.removeLast();*/
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
    public static double mean(double[] array) {  // 산술 평균 구하기
        double sum = 0.0;
        for (int i = 0; i < array.length; i++)
            sum += array[i];
        return sum / array.length;
    }

    public static double standardDeviation(double[] array, int option) {
        if (array.length < 2) return Double.NaN;
        double sum = 0.0;
        double sd = 0.0;
        double diff;
        double meanValue = mean(array);
        for (int i = 0; i < array.length; i++) {
            diff = array[i] - meanValue;
            sum += diff * diff;
        }
        sd = Math.sqrt(sum / (array.length - option));
        return sd;
    }

}
