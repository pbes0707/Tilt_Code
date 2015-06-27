package com.tiltcode.tiltcode;

import android.app.Application;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class TiltCodeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    void init(){

        Util.context = getBaseContext();

    }

}
