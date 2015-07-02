package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tiltcode.tiltcodemanager.R;
import com.tiltcode.tiltcodemanager.Util;

/**
 * Created by JSpiner on 2015. 6. 21..
 */
public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    void init(){

        ((TextView)findViewById(R.id.tv_main_name)).setText(Util.getAccessToken().getName()+"님");
        ((TextView)findViewById(R.id.tv_main_point)).setText("현재 잔금 : "+Util.getAccessToken().getPoint()+"P");

        ((Button)findViewById(R.id.btn_main_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.btn_main_list)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CouponListActivity.class);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.btn_main_purchase)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PurchaseActivity.class);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.btn_main_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
