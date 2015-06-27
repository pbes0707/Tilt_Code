package com.tiltcode.tiltcode.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Service.ServiceMonitor;

import android.os.Handler;
import android.os.Message;

/**
 * Created by JSpiner on 2015. 6. 15..
 * Contact : jspiner@naver.com
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                SplashActivity.this.finish();

                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
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
