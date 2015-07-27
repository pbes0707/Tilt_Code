package com.tiltcode.tiltcode.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by JSpiner on 2015. 7. 15..
 */
public class RestartService extends BroadcastReceiver {

    public static final String ACTION_RESTART_PERSISTENTSERVICE
            = "ACTION.Restart.PersistentService";


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("RestartService", "RestartService called!!!!!!!!!!!!!!!!!!!!!!!");


        Intent i = new Intent(context, TiltService.class);
        context.startService(i);
        /*
        if (intent.getAction().equals(ACTION_RESTART_PERSISTENTSERVICE)) {

            Log.d("RestartService", "Service dead, but resurrection");

            Intent i = new Intent(context, TiltService.class);
            context.startService(i);
        }

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            Log.d("RestartService", "ACTION_BOOT_COMPLETED");

            Intent i = new Intent(context, TiltService.class);
            context.startService(i);
        }*/
    }
}
