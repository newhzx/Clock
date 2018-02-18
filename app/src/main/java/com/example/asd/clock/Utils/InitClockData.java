package com.example.asd.clock.Utils;

import android.util.Log;

import java.util.List;

public class InitClockData {
    //上下午集合
    public static List<String> getLoopViewLunch(List<String> list){
        list.add("上午");
        list.add("下午");
        return  list;
    }
    //时集合
    public static List<String> getLoopViewHour(List<String> list){
        list.add(12+"");
        for (int i=1;i<12;i++){
            list.add(i+"");
        }
        return list;
    }

    public static void print(List<String> list){
        for (int i =0;i<list.size();i++){
            Log.i("ListItem",i+"#"+list.get(i));
        }
    }
    //分集合
    public static List<String> getLoopViewMinute(List<String> list){
        for (int i=0;i<60;i++){
            if(i<10){
                list.add("0"+i);
            }else{
                list.add(i+"");
            }
        }
        return list;
    }
    //分集合
    public static List<String> getLoopViewHourInTimer(List<String> list){
        for (int i=0;i<24;i++){
            list.add(i+"小时");
        }
        return list;
    }

    //分和秒集合
    public static List<String> getLoopViewMinuteInTimer(List<String> list){
        for (int i=0;i<60;i++){
            list.add(i+"分");
        }
        return list;
    }

    public static List<String> getLoopViewSecondInTimer(List<String> list){
        for (int i=0;i<60;i++){
            list.add(i+"秒");
        }
        return list;
    }
}
