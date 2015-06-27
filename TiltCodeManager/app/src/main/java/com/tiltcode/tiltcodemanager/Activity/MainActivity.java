package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tiltcode.tiltcodemanager.R;

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

        ((Button)findViewById(R.id.btn_main_register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ((Button)findViewById(R.id.btn_main_list)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CouponListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ((Button)findViewById(R.id.btn_main_purchase)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PurchaseActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ((Button)findViewById(R.id.btn_main_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }



}
