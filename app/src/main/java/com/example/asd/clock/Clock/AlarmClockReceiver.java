package com.example.asd.clock.Clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.asd.clock.Fragment.Bean.Clock;
import com.example.asd.clock.Utils.Global;

//接收广播 待发送到服务 再启动Activity
public class AlarmClockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
        }*/
        String action = intent.getAction();
        Clock clock = (Clock) intent.getSerializableExtra("clock");
        int position = intent.getIntExtra("position",0);
        if(action.equals(Global.Action+"."+clock.getId())) {
            Toast.makeText(context, "收到定时广播", Toast.LENGTH_SHORT).show();
            Log.i("AlarmClockReceiver", clock.getId()+"");
            //开启一个服务
            Intent intentService = new Intent(context, AlarmClockService.class);
            intentService.putExtra("clock",clock);
            intentService.putExtra("position",position);
            intentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(intentService);
        }
    }
}
