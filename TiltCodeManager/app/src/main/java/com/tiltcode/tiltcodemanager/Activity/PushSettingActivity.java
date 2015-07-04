package com.tiltcode.tiltcodemanager.Activity;

import android.content.Context;
import android.os.Bundle;

import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.View.ActionActivity;


/**
 * Created by JSpiner on 2015. 7. 4..
 */
public class PushSettingActivity extends ActionActivity {

    //로그에 쓰일 tag
    public static final String TAG = PushSettingActivity.class.getSimpleName();

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