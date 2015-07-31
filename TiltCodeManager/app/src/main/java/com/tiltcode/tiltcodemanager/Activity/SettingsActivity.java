package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tiltcode.tiltcodemanager.Adapter.SettingListAdapter;
import com.tiltcode.tiltcodemanager.Model.LoginResult;
import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;
import com.tiltcode.tiltcodemanager.View.ActionFragmentActivity;

import java.util.ArrayList;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 6. 21..
 */
public class SettingsActivity extends ActionFragmentActivity {

    //로그에 쓰일 tag
    public static final String TAG = SettingsActivity.class.getSimpleName();


    ListView listView;
    SettingListAdapter adapter;

    String[] strArray = {"푸쉬설정","비밀번호변경","로그아웃"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initActionBar();
        setEnableBack(true);

        init();
    }

    void init(){

        ArrayList<String> arrList = new ArrayList<>();
        for(int i=0;i<strArray.length;i++){
            arrList.add(strArray[i]);
        }

        listView = (ListView)findViewById(R.id.lv_setting);
        adapter = new SettingListAdapter(getBaseContext(),arrList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    Intent intent = new Intent(SettingsActivity.this, PushSettingActivity.class);
                    startActivity(intent);
                }
                else if(i==1){
                    Intent intent = new Intent(SettingsActivity.this, ChangePasswdActivity.class);
                    startActivity(intent);
                }
                else if(i==2){

                    Util.getEndPoint().setPort("40001");
                    Util.getHttpSerivce().logOut(Util.getAccessToken().getToken(),
                            new Callback<LoginResult>() {
                                @Override
                                public void success(LoginResult loginResult, Response response) {
                                    //Log.d(TAG,"logout success");

                                    if (loginResult.code.equals("-1")) {
                                        Toast.makeText(getBaseContext(), getResources().getText(R.string.message_not_enough_data), Toast.LENGTH_LONG).show();
                                    } else if (loginResult.code.equals("-2")) {
                                        Toast.makeText(getBaseContext(), getResources().getText(R.string.message_session_invalid), Toast.LENGTH_LONG).show();
                                    } else {

                                        ((Activity)MainActivity.context).finish();
                                        finish();

                                        Util.getAccessToken().destroyToken();

                                        Intent i = getBaseContext().getPackageManager()
                                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
/*
                                        Intent mStartActivity = new Intent(getBaseContext(), SplashActivity.class);
                                        int mPendingIntentId = 1111;
                                        PendingIntent mPendingIntent = PendingIntent.getActivity(getBaseContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                        AlarmManager mgr = (AlarmManager)getBaseContext().getSystemService(Context.ALARM_SERVICE);
                                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                        System.exit(0);*/
                                    }

                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.e(TAG, "Logout failure : " + error.getMessage());
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.message_network_error), Toast.LENGTH_LONG).show();
                                }
                            });

                    return;
                }

            }
        });

    }

}
