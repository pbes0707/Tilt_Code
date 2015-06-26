package com.tiltcode.tiltcode.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tiltcode.tiltcode.Adapter.MainPagerAdapter;
import com.tiltcode.tiltcode.Adapter.SettingListAdapter;
import com.tiltcode.tiltcode.Model.LoginResult;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 6. 20..
 */
public class SettingAccountActivity extends Activity {

    //로그에 쓰일 tag
    public static final String TAG = SettingAccountActivity.class.getSimpleName();

    private String[] strArray = {"이름변경","비밀번호변경","로그아웃"};
    private Class [] clsArray = {ChangeNameActivity.class, ChangePasswdActivity.class};

    int layoutid;
    Context context;

    ListView listView;
    SettingListAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);


        init();
    }

    void init() {

        this.context = getBaseContext();
        listView = (ListView)findViewById(R.id.lv_setting_account);

        ArrayList<String> arr = new ArrayList<>();
        for(int i=0;i<strArray.length;i++){
            arr.add(strArray[i]);
        }

        listAdapter = new SettingListAdapter(context, arr);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==2){

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

                                        Util.getAccessToken().destroyToken();

                                        Intent mStartActivity = new Intent(context, SplashActivity.class);
                                        int mPendingIntentId = 1111;
                                        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                                        System.exit(0);
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

                Intent intent = new Intent(context, clsArray[i]);
                startActivity(intent);
            }
        });


    }

}
