package com.example.asd.clock.Clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

import com.example.asd.clock.Fragment.Adapter.ClockAdapter;
import com.example.asd.clock.Fragment.Bean.Clock;
import com.example.asd.clock.Utils.ClockXMLUtils;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BootCompleteReceiver";
    public static final String ALARM_CLOCK_RING_ONLY_ONCE = "只响一次";
    public static List<Boolean> listChoose = new ArrayList<>();
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        List<Clock> clockInfos = null;
        try {
            clockInfos = ClockXMLUtils.readClock();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences sp = context.getSharedPreferences("chooseList",Context.MODE_PRIVATE);
        String chooseList = sp.getString("choose","");
        listChoose = ClockAdapter.String2List(chooseList);
        for (int i = 0 ;i<listChoose.size();i++) {
            if(listChoose.get(i)){
                Clock clock = clockInfos.get(i);
                Intent alarmIntent = new Intent(context, AlarmClockReceiver.class);
                alarmIntent.putExtra("clock",clock);
                alarmIntent.putExtra("position",i);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, clock.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                long firstTime = SystemClock.elapsedRealtime();
                long systemTime = System.currentTimeMillis();//系统时间
                Calendar c = Calendar.getInstance();
                // 根据用户选择时间来设置Calendar对象
                c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
                c.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
                c.set(Calendar.SECOND, c.get(Calendar.SECOND));
                // 设置AlarmManager将在Calendar对应的时间启动指定组件
                long alarmTime = c.getTimeInMillis();
                if (systemTime > alarmTime){
                    c.add(Calendar.DAY_OF_YEAR,1);
                    alarmTime =c.getTimeInMillis();
                }
                long time = alarmTime - systemTime;
                firstTime+= time;
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        firstTime, pendingIntent);

            }

        }
    }
}
