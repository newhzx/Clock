package com.example.asd.clock.Utils;

/**
 * Created by asd on 2018/1/4.
 */

public class Global {
    //所有城市的api接口
    public final static String GLOBAL = "http://api.k780.com/?app=time.world_city&appkey=30862&sign=ef2f954d2b5833e714c992c4b5f1f2d4&format=json";
    //查询某个城市的开头
    public final static String BEGIN = "http://api.k780.com/?app=time.world&city_en=";
    //查询某个城市的结尾
    public final static String END = "&appkey=30862&sign=ef2f954d2b5833e714c992c4b5f1f2d4&format=json";
    //写入xml需要的编码类型
    public final static String ENCRYPT= "UTF-8";
}