package com.example.asd.clock.Utils;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

public class VibratorUtils {
    private static Vibrator vib;
    //以pattern[]方式震动
    public static void vibrate(final Activity activity, long[] pattern,int repeat){
        if(vib == null){
            vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        }
        vib.vibrate(pattern,repeat);
    }
    //取消震动
    public static void virateCancle(final Activity activity){
        if(vib == null){
            vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        }
        vib.cancel();
        vib = null;
    }
}
