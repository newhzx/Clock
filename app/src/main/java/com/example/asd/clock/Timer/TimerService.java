package com.example.asd.clock.Timer;

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

import com.example.asd.clock.Utils.Global;
import com.example.asd.clock.Utils.SoundUtils;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.List;

public class TimerService extends Service{
    public static final String TAG = "TimerService";
    private AlarmManager alarmManager;//闹钟管理对象
    private Intent toReceiverIntent,alarmClockIntent;//跳转意图
    private Vibrator vib;//振动对象
    private long totalTimer;//总时间
    private MediaPlayer mMediaPlayer;//播放对象
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TimerServiceProvider();
    }
    @Override
    public void onCreate() {
        Log.i(TAG, "--------------onCreate----------");
        super.onCreate();
        //闹钟管理对象实例化
        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //振动对象实例化
        vib = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        totalTimer = intent.getLongExtra("totalTimer",0);
        Log.i("onStartCommand",totalTimer+"");
        //在闹铃界面点击 暂停再响 或 关闭 时 发送的意图
        toReceiverIntent =  new Intent(this , TimerReceiver.class);

        //发送到 闹铃界面的意图
        alarmClockIntent = new Intent(this, TimerActivity.class);
        alarmClockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmClockIntent.putExtra("totalTimer",totalTimer);
        startActivity(alarmClockIntent);

        SoundUtils.initMusic(TimerService.this);
        //获取铃声选项
        List<Uri> alert = SoundUtils.getListRingtones();
        int temps = TimerMusicAdapter.temp;//选择铃声的选项
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
            long[] via = Global.getTimerVibrator;//获取自定义震动
            vib.vibrate(via,2);//启动振动和重复2遍
        return super.onStartCommand(intent, flags, startId);
    }
    //解绑服务
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "--------------onUnbind----------");
        return super.onUnbind(intent);
    }
    //销毁服务
    @Override
    public void onDestroy() {
        Log.i(TAG, "--------------onDestroy----------");
        super.onDestroy();
    }
    //取消振动
    public void virateCancle(){
        if(vib !=null){
            vib.cancel();
            vib = null;
        }
    }
    //停止播放
    public void stopRing(){
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    private class TimerServiceProvider extends Binder implements ITimerServiceProvider {
        public void pauseTimerRing() {
            stopRing();
            virateCancle();
           /* PendingIntent pi = PendingIntent.getBroadcast(TimerService.this, 10, toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+totalTimer*1000,pi);
           */
        }

        public void close() throws IOException, DocumentException {
            stopRing();
            virateCancle();
            alarmManager.cancel(PendingIntent.getBroadcast(TimerService.this, 0, toReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT));
//                ClockXMLUtils.modifyNodeInClockXML(clock.getId(),false);
        }
    }
}
