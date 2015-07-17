package com.tiltcode.tiltcodemanager;

import android.app.Application;

/**
 * Created by JSpiner on 2015. 6. 18..
 */
public class TiltCodeApplication extends Application {

    /*
    app 실행시 처음 시작되는부분
     */

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    void init(){

        Util.context = getBaseContext();

    }

}
