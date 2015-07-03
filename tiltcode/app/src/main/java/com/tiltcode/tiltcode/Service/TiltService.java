package com.tiltcode.tiltcode.Service;

/**
 * Created by Secret on 2015. 6. 16..
 */
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.tiltcode.tiltcode.Activity.LockScreenActivity;
import com.tiltcode.tiltcode.Activity.MainActivity;
import com.tiltcode.tiltcode.Activity.SplashActivity;
import com.tiltcode.tiltcode.Model.AccelData;
import com.tiltcode.tiltcode.Model.Coupon;
import com.tiltcode.tiltcode.Model.LoginResult;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * User: Secret
 * Date: 2015. 6. 18.
 * Time: 오후 6:02
 */
public class TiltService extends Service implements SensorEventListener {

    private final String LOG_NAME = TiltService.class.getSimpleName();

    public static Thread mThread;
    private static int dt = 0, searchdt = 0, count = 0;
    private static boolean checkFlag = false;
    private static AccelData prev = null, now = null;
    private float TOLERANCE_VALUE = 1.5f;
    private float SEARCH_VALUE = 1.8f;
    private int RECOGNIZE = 4000;
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

        /*
        tilt
         */

        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // 자이로 센서 리스너 오브젝트를 등록
        mSensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // 가속도 센서 리스너 오브젝트를 등록
        mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        serviceRunning = true;

        //gps
        //gps manager에 대한 설정
        initializeLocationManager();


        //gps에 대한 기본 설정 반복시간등
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(LOG_NAME, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(LOG_NAME, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(LOG_NAME, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(LOG_NAME, "gps provider does not exist " + ex.getMessage());
        }
    }

    //GPS서비스
    private void initializeLocationManager() {
        Log.e(LOG_NAME, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onDestroy() {
        serviceRunning = false;
        mSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    boolean isScreenOn(){
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        return isScreenOn;
    }

    void showNotification(){
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.mipmap.ic_launcher, "Nomal Notification", System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE ;
        notification.number = 13;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, "Nomal Title", "Nomal Summary", pendingIntent);
        nm.notify(1234, notification);
    }

    private void getGPSCoupon(String tilt){
        if(mLastLocation ==null) return;

        Util.getEndPoint().setPort("40003");
        Util.getHttpSerivce().backgroundCouponGetList(Util.getAccessToken().getToken(),
                String.valueOf(mLastLocation.getLatitude()),
                String.valueOf(mLastLocation.getLongitude()),
                tilt,
                new Callback<LoginResult>() {
                    @Override
                    public void success(LoginResult loginResult, Response response) {

                        if (loginResult.code.equals("1")) {

                            if (isScreenOn()) {
                                showNotification();
                            } else {
                                Intent dialogIntent = new Intent(getApplicationContext(), LockScreenActivity.class);
                                startActivity(dialogIntent);
                            }

                        } else if (loginResult.code.equals("-1")) { //생략된 내용이 있음
                            Log.d(LOG_NAME, "background get gps coupon error : no entry");
                        } else if (loginResult.code.equals("-2")) { //받아올 쿠폰이 하나도 없음
                            Log.d(LOG_NAME, "background get gps coupon error : no coupon");
                        } else if (loginResult.code.equals("-3")) {//세션이 유효하지 않음
                            Log.d(LOG_NAME, "background get gps coupon error : invalid session");
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(LOG_NAME, "error background get gps coupon");
                    }
                });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (serviceRunning) {
                        SystemClock.sleep(30);

                        if(checkFlag)
                        {
                            Log.d("s", "dt : " + String.valueOf(dt));
                            dt += 30;
                            searchdt -= 30;
                        }



                        if(dt > RECOGNIZE)
                        {
                            for(int i = 1 ; i<Arr_Accel.length ; i++)
                            {
                                /*if( (Arr_Accel[i][0] - SENSITIVE_SEARCH_VALUE < now.x && Arr_Accel[i][0] + SENSITIVE_SEARCH_VALUE > now.x ) &&
                                        (Arr_Accel[i][1] - SENSITIVE_SEARCH_VALUE < now.y && Arr_Accel[i][1] + SENSITIVE_SEARCH_VALUE > now.y ) &&
                                        (Arr_Accel[i][2] - SENSITIVE_SEARCH_VALUE < now.z && Arr_Accel[i][2] + SENSITIVE_SEARCH_VALUE > now.z ) )
                                {
                                    Log.d(LOG_NAME, "Sensor Tilt : " + String.valueOf(i + 1));
                                    dt = 0;

                                    ////////////////정밀 검사 부분///////////////

                                }*/
                                if( (Arr_Accel[i][0] - SEARCH_VALUE < now.x && Arr_Accel[i][0] + SEARCH_VALUE > now.x ) &&
                                        (Arr_Accel[i][1] - SEARCH_VALUE < now.y && Arr_Accel[i][1] + SEARCH_VALUE > now.y ) &&
                                        (Arr_Accel[i][2] - SEARCH_VALUE < now.z && Arr_Accel[i][2] + SEARCH_VALUE > now.z ) )
                                {
                                    Log.d(LOG_NAME, "Tilt : " + String.valueOf(i + 1));
                                    if(isScreenOn()) getGPSCoupon(String.valueOf(i+1));

                                }
                            }
                            dt = 0;
                            checkFlag = false;
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
                    prev = now = v;
                }
                else
                {
                    now = v;
                    if(checkFlag == false)
                    {
                        if( (Arr_Accel[0][0] - SEARCH_VALUE < now.x && Arr_Accel[0][0] + SEARCH_VALUE > now.x ) &&
                                (Arr_Accel[0][1] - SEARCH_VALUE < now.y && Arr_Accel[0][1] + SEARCH_VALUE > now.y ) &&
                                (Arr_Accel[0][2] - SEARCH_VALUE < now.z && Arr_Accel[0][2] + SEARCH_VALUE > now.z ) )
                        {
                            Log.d("s", "checkFlag true");
                            checkFlag = true;
                            searchdt = 2000;
                            prev = now;
                        }
                    }
                    else
                    {
                        if(searchdt > 0)
                            prev = now;
                        else if( !((prev.x - TOLERANCE_VALUE < v.x && prev.x + TOLERANCE_VALUE > v.x)
                                && (prev.y - TOLERANCE_VALUE < v.y && prev.y + TOLERANCE_VALUE > v.y)
                                && (prev.z - TOLERANCE_VALUE < v.z && prev.z + TOLERANCE_VALUE > v.z)))
                        {
                            Log.d("s", "checkFlag false");
                            checkFlag = false;
                            dt = 0;
                        }
                    }
                }

                /*Log.d("sensor","Accel X : " + Math.round(prev.x*100d) / 100d +
                        " Y : " + Math.round(prev.y*100d) / 100d +
                        " Z : " + Math.round(prev.z*100d) / 100d);*/
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


    private LocationManager mLocationManager = null; //LocationManager 변
    private static final int LOCATION_INTERVAL = 1000; //gps업데이트 주기(1000=1초)
    private static final float LOCATION_DISTANCE = 10f; // gps distance
    Location mLastLocation; //마지막으로 수신된 gps의 주소

    class LocationListener implements android.location.LocationListener{
        //생성자
        public LocationListener(String provider)
        {
            Log.e(LOG_NAME, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        //gps좌표가 변경되었을때
        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(LOG_NAME, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }
        //gps가 disabled됬을때
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(LOG_NAME, "onProviderDisabled: " + provider);
        }
        //gps가 enable됬을때
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(LOG_NAME, "onProviderEnabled: " + provider);
        }
        //gps의 상태가 변경되었을때
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(LOG_NAME, "onStatusChanged: " + provider);
        }
    }
    //앱에서 사용할 gps수신방식, gps를 이용한 방식과 네트워크를 이용한방식 둘다 사용
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

}
