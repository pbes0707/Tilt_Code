package com.tiltcode.tiltcode.Activity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tiltcode.tiltcode.R;
import com.tiltcode.tiltcode.Util;
import com.tiltcode.tiltcode.View.ActionActivity;

/**
 * Created by JSpiner on 2015. 7. 15..
 */
public class ServiceSettingActivity extends ActionActivity {

    CheckBox cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicesetting);

        initActionBar();
        setEnableBack(true);

        cb = ((CheckBox)findViewById(R.id.cb_service_onoff));

        init();
    }

    void init(){
        cb.setChecked(Util.getBoolean("serviceonoff", true));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Util.putBoolean("serviceonoff",b);
            }
        });
    }
}
