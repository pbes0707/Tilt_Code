package com.tiltcode.tiltcode.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.tiltcode.tiltcode.Adapter.SettingListAdapter;
import com.tiltcode.tiltcode.Model.LoginResult;
import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;
import com.tiltcode.tiltcode.View.ActionActivity;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JSpiner on 2015. 7. 4..
 */
public class PushSettingActivity  extends ActionActivity {

    //로그에 쓰일 tag
    public static final String TAG = SettingAccountActivity.class.getSimpleName();

    int layoutid;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushsetting);

        super.initActionBar();
        setEnableBack(true);

        init();
    }

    void init() {

        this.context = getBaseContext();


    }
}