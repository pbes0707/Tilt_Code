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

import com.tiltcode.tiltcode.Activity.CouponReceiveActivity;
import com.tiltcode.tiltcode.Activity.LockScreenActivity;
import com.tiltcode.tiltcode.Activity.MainActivity;
import com.tiltcode.tiltcode.Activity.SplashActivity;
import com.tiltcode.tiltcode.Model.AccelData;
import com.tiltcode.tiltcode.Model.Coupon;
import com.tiltcode.tiltcode.Model.CouponResult;
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
    private float TOLERANCE_VALUE = 1.6f;
    private float SEARCH_VALUE = 2.0f;
    private int RECOGNIZE = 3000;
    private static float[][] Arr_Accel = {
            {9999f, 9999f, 9999f}, // 1 exclude {0f, 9.8f, 0f}
            {6.9f, 6.8f, 0f},
            {9.8f, 0f, 0f},
            {6.8f, -6.8f, 0f},
            {0f, -9.8f, 0f}, // 5
            {-6.8f, -6.8f, 0f},
            {-9.8f, 0f, 0f},
            {-6.8f, 6.8f, 0f},
            {9999f, 9999f, 9999f}, // 9 {0f, 6.8f, 6.8f}
            {9999f, 9999f, 9999f}, // 10 exclude {0f, 0f, 9.8f}
            {0f, -6.8f, 6.8f},
            //{0f, -9.8f, 0f}, // 12 Overlap with 5
            {0f, -6.8f, -6.8f}, // Fixed 12 , prev 13
            {0f, 0f, -9.8f},
            {0f, 6.8f, -6.8f}
    };

    private ComponentName recentComponentName;
    private ActivityManager mActivityManager;
    private SensorManager mSensorManager;
    private Sensor gyroSensor;
    private Sensor accelerometerSensor;

    public static List<Coupon> couponList;

    private boolean serviceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();

        /*
        tilt
         */
        Notification notification = new Notification(R.drawable.ic_tilt, "서비스 실행됨", System.currentTimeMillis());
        Intent notificationIntent = new Intent(getApplicationContext(), SplashActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notification.flags  |= Notification.FLAG_NO_CLEAR;
        notification.priority = Notification.PRIORITY_MIN;

        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, 0);

//        notification.setLatestEventInfo(context, title, message, intent);
        notification.setLatestEventInfo(getApplicationContext(), "Tilt Service", "Foreground로 실행됨", intent);
//        startForeground(1, notification);

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
//        ServiceMonitor.getInstance().startMonitoring(conte);
    }

    boolean isScreenOn(){
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        return isScreenOn;
    }

    void showNotification(){
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.mipmap.ic_launcher, "틸트 감지됨", System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE ;
        notification.number = 1;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, CouponReceiveActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, "쿠폰도착", "새로운 쿠폰을 확인해주세요.", pendingIntent);
        nm.notify(1175, notification);

        android.os.Handler h = new android.os.Handler();
        long delayInMilliseconds = 15000;
        h.postDelayed(new Runnable() {
            public void run() {
                ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1175);
            }
        }, delayInMilliseconds);
    }

    private void getGPSCoupon(String tilt){

        if(mLastLocation ==null) {
            Log.d(LOG_NAME,"location null");
            return;
        }
        else{
            Log.d(LOG_NAME,"tilt : "+tilt+"lat : "+mLastLocation.getLatitude()+" lng : "+mLastLocation.getLongitude()+"token : "+Util.getAccessToken().getToken());
        }

        Util.getEndPoint().setPort("40003");
        Util.getHttpSerivce().backgroundCouponGetList(Util.getAccessToken().getToken(),
                String.valueOf(mLastLocation.getLatitude()),
                String.valueOf(mLastLocation.getLongitude()),
                tilt,
                new Callback<CouponResult>() {
                    @Override
                    public void success(CouponResult couponResult, Response response) {
                        Log.d(LOG_NAME,"backgroundcouponget success : "+couponResult.code);

                        if (couponResult.code.equals("1")) {
                            couponList = couponResult.coupon;

                            if (isScreenOn()) {
                                showNotification();
                            } else {
                                Intent dialogIntent = new Intent(getApplicationContext(), LockScreenActivity.class);
                                dialogIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                startActivity(dialogIntent);
                            }

                        } else if (couponResult.code.equals("-1")) { //생략된 내용이 있음
                            Log.d(LOG_NAME, "background get gps coupon error : no entry");
                        } else if (couponResult.code.equals("-2")) { //받아올 쿠폰이 하나도 없음
                            Log.d(LOG_NAME, "background get gps coupon error : no coupon");
                        } else if (couponResult.code.equals("-3")) {//세션이 유효하지 않음
                            Log.d(LOG_NAME, "background get gps coupon error : invalid session");
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(LOG_NAME, "error background get gps coupon"+error.getMessage());
                    }
                });

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Log.d(LOG_NAME,"onStartCommand onoff : "+Util.getBoolean("serviceonoff", true));

        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (serviceRunning) {
                        SystemClock.sleep(30);
                        if(!Util.getBoolean("serviceonoff", true)) continue;



                        dt += 30;
                        if(dt%30==2){

                            Log.d(LOG_NAME,"service running : "+dt);
                        }
                        if(dt > RECOGNIZE)
                        {
                            for(int i = 0 ; i<Arr_Accel.length ; i++)
                            {
                                if( (Arr_Accel[i][0] - SEARCH_VALUE < now.x && Arr_Accel[i][0] + SEARCH_VALUE > now.x ) &&
                                        (Arr_Accel[i][1] - SEARCH_VALUE < now.y && Arr_Accel[i][1] + SEARCH_VALUE > now.y ) &&
                                        (Arr_Accel[i][2] - SEARCH_VALUE < now.z && Arr_Accel[i][2] + SEARCH_VALUE > now.z ) )
                                {
                                    Log.d("sensor", "Tilt : " + String.valueOf(i + 1));
                                    /////
                                    getGPSCoupon(String.valueOf(i+1));

                                }
                            }
                            dt = 0;
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
                    prev = now = v;
                else
                {
                    if( !((prev.x - TOLERANCE_VALUE < v.x && prev.x + TOLERANCE_VALUE > v.x)
                            && (prev.y - TOLERANCE_VALUE < v.y && prev.y + TOLERANCE_VALUE > v.y)
                            && (prev.z - TOLERANCE_VALUE < v.z && prev.z + TOLERANCE_VALUE > v.z)))
                    {

                        dt = 0;
                        prev = v;
                    }
                    now = v;
                }
                break;
            }

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
