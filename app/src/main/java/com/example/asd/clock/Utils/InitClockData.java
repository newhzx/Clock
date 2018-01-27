package com.example.asd.clock.Utils;

import java.util.ArrayList;
import java.util.List;

public class InitClockData {
    //上下午集合
    public static ArrayList<String> getLoopViewLunch(List<String> list){
        list.add("上午");
        list.add("下午");
        return (ArrayList<String>) list;
    }
    //时集合
    public static ArrayList<String> getLoopViewHour(List<String> list){
        list.add("12");
        for (int i=1;i<12;i++){
            list.add(i+"");
        }
        return (ArrayList<String>)list;
    }
    //分集合
    public static ArrayList<String> getLoopViewMinute(List<String> list){
        for (int i=0;i<60;i++){
            if(i<10){
                list.add("0"+i);
            }else{
                list.add(i+"");
            }
        }
        return (ArrayList<String>)list;
    }
}
