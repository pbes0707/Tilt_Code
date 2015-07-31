package com.tiltcode.tiltcode.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Service.ServiceMonitor;
import com.tiltcode.tiltcode.Util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by JSpiner on 2015. 6. 15..
 * Contact : jspiner@naver.com
 */
public class SplashActivity extends Activity {

    //로그에 쓰일 tag
    public static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                SplashActivity.this.finish();

                Intent intent = new Intent(SplashActivity.this,LoginSelectActivity.class);
                startActivity(intent);
            }
        };

        ServiceMonitor serviceMonitor = ServiceMonitor.getInstance();
        if (serviceMonitor.isMonitoring() == false)
        {
            serviceMonitor.startMonitoring(getApplicationContext());
        }


        handler.sendEmptyMessageDelayed(0,1500);


    }
}
