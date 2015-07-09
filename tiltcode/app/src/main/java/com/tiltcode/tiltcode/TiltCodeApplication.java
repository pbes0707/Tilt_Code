package com.tiltcode.tiltcode;

import android.app.Application;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

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


        Picasso.Builder builder = new Picasso.Builder(this);
//        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(false);
        Picasso.setSingletonInstance(built);
    }

}
