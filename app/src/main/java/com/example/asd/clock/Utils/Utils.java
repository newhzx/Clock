package com.example.asd.clock.Utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.asd.clock.Fragment.Bean.WorldClock;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    private static List<WorldClock> lists;
    public static void setLists(List<WorldClock> list){
        Utils.lists = list;
    }
    public static List<WorldClock> getLists(){
        return lists;
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
            file = new File(SDpath.getAbsolutePath(), xml);
        }
        return file;
    }
    //判断某文件是否存在 不存在就创建
    public static File isNotExistCreateFile(String xml) throws IOException {
        File file = getFile(xml);
        //不存在就创建
        if (!file.exists()) {
            file.createNewFile();
            Log.i("isFileExist", "文件不存在");
            //创建文档的根节点
            Document document = DocumentHelper.createDocument();
            //创建文档的 根元素节点
            Element root = DocumentHelper.createElement("citys");
            //设置根节点
            document.setRootElement(root);
            //写入到xml文件中
            XMLUtils.Writer(document,file);
        }else {
            Log.i("isFileExist", "文件已经存在");
        }
        return file;
    }


}