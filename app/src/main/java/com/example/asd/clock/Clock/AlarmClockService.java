package com.example.asd.clock.Clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.asd.clock.Fragment.Adapter.ClockAdapter;
import com.example.asd.clock.Fragment.Bean.Clock;
import com.example.asd.clock.Utils.Global;
import com.example.asd.clock.Utils.SoundUtils;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.List;

public class AlarmClockService extends Service{
    public static final String TAG = "AlarmClockService";
    public static final String ALARM_CLOCK_RING_ONLY_ONCE = "只响一次";
    private AlarmManager alarmManager;
    private Intent toReceiverIntent;
    private Vibrator vib;
    private Clock clock;
    private MediaPlayer mMediaPlayer;
    private int position;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AlarmClockServiceProvider();
    }
    @Override
    public void onCreate() {
        Log.i(TAG, "--------------onCreate----------");
        super.onCreate();
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        vib = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "--------------onStartCommand----------");
        clock = (Clock) intent.getSerializableExtra("clock");
        position = intent.getIntExtra("position",0);
        //在闹铃界面点击 暂停再响 或 关闭 时 发送的意图
        toReceiverIntent =  new Intent(this , AlarmClockReceiver.class);
        toReceiverIntent.setAction(Global.Action+"."+clock.getId());
        toReceiverIntent.putExtra("clock",clock);
        toReceiverIntent.putExtra("position",position);
        //发送到 闹铃界面的意图
        Intent alarmClockIntent = new Intent(this, AlarmClockActivity.class);
        alarmClockIntent.putExtra("clock",clock);
        alarmClockIntent.putExtra("position",position);
        alarmClockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(alarmClockIntent);

        SoundUtils.initMusic(AlarmClockService.this);
        //获取铃声选项
        List<Uri> alert = SoundUtils.getListRingtones();
        int temps = AddClockMusicAdapter.temp;
        if(temps!=-1){
            Uri ringtone = alert.get(temps);
            try {
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();
                }
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(this, ringtone);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long[][] vibrator = Global.getVibratorList;
        int index = AddClockVibratorAdapter.vibrator;
        if(index!=-1){
            long[] vibrators = vibrator[index];
            vib.vibrate(vibrators,0);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "--------------onUnbind----------");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "--------------onDestroy----------");
        super.onDestroy();
    }

    public void virateCancle(){
        if(vib !=null){
            vib.cancel();
            vib = null;
        }
    }
    public void stopRing(){
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    private class AlarmClockServiceProvider extends Binder implements IAlarmClockServiceProvider {
        public void pause9MinRing() {
            stopRing();
            virateCancle();
            int alarmClockId = clock.getId();
            PendingIntent pi = PendingIntent.getBroadcast(AlarmClockService.this, alarmClockId, toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+9*60*1000, pi);
            Log.i(TAG, "----------9分钟后响铃的广播发送完毕！-----------");
        }

        public void close() throws IOException, DocumentException {
            stopRing();
            virateCancle();
            int alarmClockId = clock.getId();
            String repeatCycle = repeatCycleMethod(clock.getJson());
            if(repeatCycle.equals(ALARM_CLOCK_RING_ONLY_ONCE)){     //只响一次的闹钟  --> 取消该广播，更新数据库
                alarmManager.cancel(PendingIntent.getBroadcast(AlarmClockService.this, alarmClockId, toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT));
//                ClockXMLUtils.modifyNodeInClockXML(clock.getId(),false);
                ClockAdapter.getChoose().put(position,false);
                Log.i(TAG, "--只响一次的闹钟广播已取消，数据库更新完毕！！-----");
            }else{          //  --> 发送下一次闹钟的广播
             /*   PendingIntent pi = PendingIntent.getBroadcast(AlarmClockService.this, alarmClockId, toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+dTime, pi);
                Log.i(TAG, "--距离下一次闹钟的剩余时间为"+"-----");*/
            }
        }
        public void nineMinClose() {
            pause9MinRing();
        }
    }

    private String repeatCycleMethod(String json) {
        Log.i(ALARM_CLOCK_RING_ONLY_ONCE,json);
        return ALARM_CLOCK_RING_ONLY_ONCE;
    }
}
