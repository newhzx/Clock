package com.example.asd.clock.Utils;

public class Global {
    //所有城市的api接口
    public final static String GLOBAL = "http://api.k780.com/?app=time.world_city&appkey=30862&sign=ef2f954d2b5833e714c992c4b5f1f2d4&format=json";
    //查询某个城市的开头
    public final static String BEGIN = "http://api.k780.com/?app=time.world&city_en=";
    //查询某个城市的结尾
    public final static String END = "&appkey=30862&sign=ef2f954d2b5833e714c992c4b5f1f2d4&format=json";
    //写入xml需要的编码类型
    public final static String ENCRYPT= "UTF-8";
    //添加时钟重复数据
    public final static String[] getWeekRepeat = {"每周日","每周一","每周二","每周三","每周四","每周五","每周六"};
    //添加铃声数据
    public final static String[] getPhoneMusic = {"雷达","波浪","灯塔"};
    //振动的名称数据
    public final static String[] getVibrator = {"提醒(默认)","断奏","快速","心跳","轻重音"};
    //振动的参数
    public final static long[][] getVibratorList = {{300L, 2000L},{500L, 500L},{200L, 1000L},{300L,300L},{100L,100L,100L,1500L}};
    //行动的包名作为广播action的一部分
    public final static String Action = "com.example.asd.intent";
    //闹钟存放xml文件名称
    public final static String AlarmName = "clockss.xml";
    //城市名称集合
    public final static String CityTime = "citytime.xml";
    //振动频率
    public final static long[] getTimerVibrator = {100L,100L,100L,200L,300L,1500L};
    //计时器倒数速率
    public final static int countDownInterval = 1000;
    //秒表刷新速率
    public final static int TIME_TO_SEND = 10;
    //秒表接收关键字
    public final static int TICK_WHAT = 2;
}