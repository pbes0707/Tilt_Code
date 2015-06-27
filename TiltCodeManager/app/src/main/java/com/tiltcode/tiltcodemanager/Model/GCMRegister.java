package com.tiltcode.tiltcodemanager.Model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tiltcode.tiltcodemanager.Activity.MainActivity;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Secret on 2015. 6. 28..
 */
public class GCMRegister
{
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String PROPERTY_REG_ID = "registration_id";

    // SharedPreferences에 저장할 때 key 값으로 사용됨.
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "TiltCode";

    String SENDER_ID = "382919675678";

    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    Context context;
    Activity activity;

    String regid;

    public GCMRegister(Context context, Activity activity)
    {
        this.context = context;
        this.activity = activity;
    }

    public void execute()
    {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(activity);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        // 앱이 업데이트 되었는지 확인하고, 업데이트 되었다면 기존 등록 아이디를 제거한다.
        // 새로운 버전에서도 기존 등록 아이디가 정상적으로 동작하는지를 보장할 수 없기 때문이다.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return activity.getSharedPreferences(activity.getClass().getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // 서버에 발급받은 등록 아이디를 전송한다.
                    // 등록 아이디는 서버에서 앱에 푸쉬 메시지를 전송할 때 사용된다.

                    sendRegistrationIdToBackend(regid);

                    // 등록 아이디를 저장해 등록 아이디를 매번 받지 않도록 한다.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
            }

        }.execute(null, null, null);
    }

    private void storeRegistrationId(Context context, String regid) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regid);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void sendRegistrationIdToBackend(String regid)
    {
        Util.getEndPoint().setPort("40001");
        Util.getHttpSerivce().register(regid, Util.getAccessToken().getUserId(),
                new Callback<LoginResult>() {
                    @Override
                    public void success(com.tiltcode.tiltcodemanager.Model.LoginResult loginResult, Response response) {
                        Log.d(TAG, "access success / code : " + loginResult.code);
                        if (loginResult.code.equals("1")) { //성공
                            Log.d(TAG, "success register");

                        } else if (loginResult.code.equals("-1")) { // 누락된게 있음
                            Toast.makeText(activity.getBaseContext(), activity.getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                        } else if (loginResult.code.equals("-2")) { // 이미 등록된 아이디일떄
                            Toast.makeText(activity.getBaseContext(), activity.getResources().getText(R.string.message_session_invalid), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, "login failure : " + error.getMessage());
                        Toast.makeText(activity.getBaseContext(), activity.getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();

                    }
                });

    }


}
