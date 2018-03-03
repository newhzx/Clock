package com.example.asd.clock.Timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.asd.clock.Utils.Global;

//接收广播 待发送到服务 再启动Activity
public class TimerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
        }*/
        String action = intent.getAction();
        String totalAction = Global.Action+".timer";//区分别的广播id
        long totalTimer = intent.getLongExtra("totalTimer",0);
        if(totalAction.equals(action)){
            Intent intentService = new Intent(context, TimerService.class);
            intentService.putExtra("totalTimer",totalTimer);
            intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intentService);
        }
    }
}
