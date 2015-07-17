package com.tiltcode.tiltcode.Service;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.tiltcode.tiltcode.Service.TiltService;

import java.util.List;

public class ServiceMonitor {

    private static ServiceMonitor instance;
    private AlarmManager am;
    private Intent intent;
    private PendingIntent sender;
    private long interval = 500;

    private ServiceMonitor() {}
    public static synchronized ServiceMonitor getInstance() {
        if (instance == null) {
            instance = new ServiceMonitor();
        }
        return instance;
    }

    public static class MonitorBR extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isRunningService(context, TiltService.class) == false) {
                context.startService(new Intent(context, TiltService.class));
            }
        }
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void startMonitoring(Context context) {
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, MonitorBR.class);
        sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, sender);
    }

    public void stopMonitoring(Context context) {
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, MonitorBR.class);
        sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(sender);
        am = null;
        sender = null;
    }

    public boolean isMonitoring() {
        return (TiltService.mThread == null || TiltService.mThread.isAlive() == false) ? false : true;
    }

    private static boolean isRunningService(Context context, Class<?> cls) {
        boolean isRunning = false;

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> info = activityManager.getRunningServices(Integer.MAX_VALUE);

        if (info != null) {
            for(ActivityManager.RunningServiceInfo serviceInfo : info) {
                ComponentName compName = serviceInfo.service;
                String className = compName.getClassName();

                if(className.equals(cls.getName())) {
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }
}
