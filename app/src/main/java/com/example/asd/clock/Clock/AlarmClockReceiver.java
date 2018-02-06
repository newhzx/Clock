package com.example.asd.clock.Clock;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

//接收广播 待发送到服务 再启动Activity
public class AlarmClockReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmClockReceiver";
    private AlarmManager alarmManager;
    public static final String AlARM_CLOCK_RING_ONLY_ONCE = "只响一次";
    @Override
    public void onReceive(Context context, Intent intent) {
        alarmManager = context.getSystemService(context.);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
        }
        // to do something
//        doSomething();
    }
}
