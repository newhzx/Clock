package com.example.asd.clock.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.asd.clock.Clock.AddClockRepeat;
import com.example.asd.clock.Clock.AddClockRepeatAdapter;
import com.example.asd.clock.Fragment.Bean.WorldClock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    //设置list的getter和setter结合
    private static List<WorldClock> list;
    public static void setList(List<WorldClock> list){
        Utils.list = list;
    }
    public static List<WorldClock> getList(){
        return list;
    }

    //设置lists的getter和setter结合
    private static List<WorldClock> listworldclocks;
    public static void setLists(List<WorldClock> listworldclocks){
        Utils.listworldclocks = listworldclocks;
    }
    public static List<WorldClock> getLists(){
        return listworldclocks;
    }
    //Toast的简单封装
    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    //判断该文件是否存在
    public static File getFile(String xml) {
        File file = null;
        //1.判定判定是否插入sd卡，并且应用程序具有读写SD卡的权限
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            //2.获取SD卡根目录
            File SDpath = Environment.getExternalStorageDirectory();
            //返回文件对象
            file = new File(SDpath.getAbsolutePath(), xml);
        }
        return file;
    }
    //判断某文件是否存在 不存在就创建
    public static File isNotExistCreateFile(String xml,String rootName) throws IOException {
        File file = getFile(xml);
        //不存在就创建
        if (!file.exists()||file==null) {
            file.createNewFile();//创建新文件
            Log.i("isFileExist", "文件不存在");
            //创建文档的根节点
            Document document = DocumentHelper.createDocument();
            //创建文档的 根元素节点
            Element root = DocumentHelper.createElement(rootName);
            //设置根节点
            document.setRootElement(root);
            //写入到xml文件中
            XMLUtils.Writer(document,file);
        }else {
            Log.i("isFileExist", "文件已经存在aaaa");
        }
        return file;
    }

    //获取重复选项选中状态的数目
    public static int getMapCount(Map<Integer, Boolean> map){
        int mapCount = 0;
        for (int x = 0 ;x<map.size();x++) {
            if (map.get(x)) ++mapCount;
        }
        return mapCount;
    }
    //将字符串类型转化为map对象
    public  static  Map<Integer,Boolean> getRepeat(String string){
        Log.i("string",string);
        Gson gson = new Gson();
        Map<Integer,Boolean> map = gson.fromJson(string,new TypeToken<HashMap<Integer,Boolean>>(){}.getType());
        return map;
    }
    //将map对象类型转化为string字符串 显示重复的字符串类型
    public static String getRepeatContent(Map<Integer, Boolean> map) {
        int count = 0;
        int currentNum = getMapCount(map);//选中的重复个数
        List<String> list = AddClockRepeat.getList();
        StringBuffer sb = new StringBuffer();
        if (currentNum == 0) {
            return "";
        } else if (currentNum == 1) {
            for (int i = 0; i < list.size(); i++) {
                if (map.get(i)) {
                    return list.get(i);
                }
            }
        } else if (currentNum > 1 && currentNum < list.size() + 1) {
            for (int i = 0; i < list.size(); i++) {
                if (map.get(i)) {
                    switch (i) {
                        case 1:
                            sb.append("周一 ");
                            break;
                        case 2:
                            sb.append("周二 ");
                            break;
                        case 3:
                            sb.append("周三 ");
                            break;
                        case 4:
                            sb.append("周四 ");
                            break;
                        case 5:
                            sb.append("周五 ");
                            break;
                        case 6:
                            sb.append("周六 ");
                            break;
                    }
                }
            }
            if (map.get(0)) {
                sb.append("周日 ");
            }
            count = AddClockRepeatAdapter.getMapCount();
            if (count == list.size()) {
                sb = new StringBuffer();
                sb.append("每天 ");
            }
            return sb.toString();
        }
        return null;
    }
}