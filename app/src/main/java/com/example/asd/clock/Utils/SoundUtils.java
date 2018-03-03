package com.example.asd.clock.Utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SoundUtils {
    private static MediaPlayer mMediaPlayer = null;
    private SoundUtils() {}
    public static MediaPlayer getmMediaPlayer(){
        if(mMediaPlayer==null){
            mMediaPlayer = new MediaPlayer();
        }
        return mMediaPlayer;
    }
    //开始播放
    public static void playRing(final Activity activity, Uri alert) {
        try {
            //判空
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            //判断是否在播放
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(activity, alert);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            //侦听播放完毕事件 播放完毕就停止振动
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    VibratorUtils.virateCancle(activity);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //暂停播放
    public static void pauseRing() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
        }
    }

    //停止播放
    public static void stopRing() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }
    //初始化铃声名称和类型集合
    private static List<String> listRong;
    private static List<Uri> listRingtones;
    public static void initMusic(Context context) {
        listRong = new ArrayList<>();
        listRingtones = new ArrayList<>();
        RingtoneManager ringtoneMgr = new RingtoneManager(context);
        ringtoneMgr.setType(RingtoneManager.TYPE_ALARM);
        Cursor alarmsCursor = ringtoneMgr.getCursor();
        while (alarmsCursor != null && !alarmsCursor.isAfterLast() && alarmsCursor.moveToNext()) {
            listRingtones.add(
                    ringtoneMgr.getRingtoneUri(alarmsCursor.getPosition()));
            listRong.add(alarmsCursor.getString(alarmsCursor.getColumnIndex("title")));
            Log.i("Uri", alarmsCursor.getString(alarmsCursor.getColumnIndex("title")));
        }
    }

    public static List<String> getListRong() {
        return listRong;
    }

    public static List<Uri> getListRingtones() {
        return listRingtones;
    }
}
