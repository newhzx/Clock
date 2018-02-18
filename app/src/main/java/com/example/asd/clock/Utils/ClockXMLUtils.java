package com.example.asd.clock.Utils;

import android.util.Log;

import com.example.asd.clock.Fragment.Bean.Clock;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClockXMLUtils {

    public static void modifyNodeInClockXML(int id,boolean bool) throws IOException, DocumentException {
        File file = Utils.isNotExistCreateFile(Global.AlarmName,"clocks");
        //判断该节点是否存在于xml中
        if (!selectNoteInClockXML(id)) {
            Log.i("selectNoteInClockXML","已经存在该节点");
            return;
        }
        SAXReader reader = new SAXReader();
        Document doc = reader.read(new FileInputStream(file));
        Element root = doc.getRootElement();

        // 取得某节点下名为"clock"的所有字节点
        List<Element> nodes = root.elements("clock");
        Element element = null;
        for (Object obj : nodes) {
            element = (Element) obj;
            // 修改指定节点下的子节点的值
            if (element.valueOf("_id") != null && id==Integer.parseInt(element.valueOf("_id"))) {
                element.element("switchStatus").setText(String.valueOf(bool));
            }
        }
        XMLUtils.Writer(doc, file);
        Log.i("isFileExist", id+"成功修改Element数据");
    }

    public static int addNoteToClockXML(int id, int lunchSelect, int hourSelect, int minuteSelect, int second, String tags, String json) throws IOException, DocumentException {
        File file = Utils.isNotExistCreateFile(Global.AlarmName,"clocks");
        //判断该节点是否存在于xml中
        if (selectNoteInClockXML(id)) {
            Log.i("selectNoteInClockXML",selectNoteInClockXML(id)+"");
            return -1;
        }
        SAXReader reader = new SAXReader();
        Document doc = reader.read(new FileInputStream(file));
        Element root = doc.getRootElement();

        Element e1 = DocumentHelper.createElement("clock");
        e1.addElement("_id").addText(id+"");
        e1.addElement("lunchSelect").addText(lunchSelect+"");
        e1.addElement("hourSelect").addText(hourSelect+"");
        e1.addElement("minuteSelect").addText(minuteSelect+"");
        e1.addElement("second").addText(second+"");
        e1.addElement("tags").addText(tags);
        e1.addElement("json").addText(json);
        e1.addElement("switchStatus").addText(String.valueOf(true));
        List<Element> list = root.elements("clock");
        int index = inSertIndex(list,id);
        list.add(index,e1);
        XMLUtils.Writer(doc, file);
        Log.i("isFileExist", "成功添加Element数据");
        return index;
    }

    private static int inSertIndex(List<Element> list, int id) {
        if(list.size()==0||id<Integer.parseInt(list.get(0).valueOf("_id"))) return 0;
        if(id>Integer.parseInt(list.get(list.size()-1).valueOf("_id"))) return list.size();
        for (int i =0;i<list.size();i++){
            int pre = Integer.parseInt(list.get(i).valueOf("_id"));
            int back = Integer.parseInt(list.get(i+1).valueOf("_id"));
            if(pre < id && id < back){return i+1;}
        }
        return 0;
    }


    //判断xml文件是否存在某节点
    public static boolean selectNoteInClockXML(int id) {
        File file = Utils.getFile(Global.AlarmName);
        Document document = null;
        SAXReader saxReader = new SAXReader(); // 用来读取xml文档
        try {
            //url是文件的地址
            document = saxReader.read(file);
            List list = document.selectNodes("//clocks");//查找指定的节点
            Iterator iterator = list.iterator();//迭代citys下所有的节点
            while (iterator.hasNext()) {
                Element ele = (Element) iterator.next();
                Iterator it = ele.elementIterator("clock");//指定到city
                while (it.hasNext()) {
                    Element es = (Element) it.next();//迭代的所有city节点
                    Log.i("taggg",id + "#" + es.valueOf("_id") + "#" + Integer.parseInt(es.valueOf("_id")));
                    if (es.valueOf("_id") != null && id == Integer.parseInt(es.valueOf("_id"))) {
                        return true;
                    }
                }
            }
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    //读取指定文件的节点 返回list集合
    public static List<Clock> readSelectedClock() throws DocumentException, IOException {
        File file = Utils.isNotExistCreateFile(Global.AlarmName,"clocks");
        SAXReader sax = new SAXReader();//创建一个SAXReader对象
        Document document = sax.read(file);//获取document对象,如果文档无节点，则会抛出Exception提前结束
        Element root = document.getRootElement();//获取根节点
        List<Clock> list = getNodes(root);//从根节点开始遍历所有节点
        return list;
    }

    //读取指定文件的节点 返回list集合
    public static List<Clock> readClock() throws DocumentException, IOException {
        File file = Utils.isNotExistCreateFile(Global.AlarmName,"clocks");
        SAXReader sax = new SAXReader();//创建一个SAXReader对象
        Document document = sax.read(file);//获取document对象,如果文档无节点，则会抛出Exception提前结束
        Element root = document.getRootElement();//获取根节点
        List<Clock> list = getNodes(root);//从根节点开始遍历所有节点
        return list;
    }

    //获取指定xml文件的所有节点并返回list集合
    public static List<Clock> getNodes(Element root) {
        List<Clock> list = new ArrayList<>();//存放节点的list集合
        List nodes = root.selectNodes("//clocks");//查找指定的节点
        Iterator iterator = nodes.iterator();//迭代citys下所有的节点
        while (iterator.hasNext()) {//迭代根节点
            Element ele = (Element) iterator.next();
            Iterator it = ele.elementIterator("clock");//指定到city
            Clock clock = null;
            while (it.hasNext()) {
                clock = new Clock();
                Element e = (Element) it.next();//迭代的所有city节点
                //设置对象存放节点内容
                clock.setId(Integer.parseInt(e.valueOf("_id")));
                clock.setLunchSelect(Integer.parseInt(e.valueOf("lunchSelect")));
                clock.setHourSelect(Integer.parseInt(e.valueOf("hourSelect")));
                clock.setMinuteSelect(Integer.parseInt(e.valueOf("minuteSelect")));
                clock.setSecond(Integer.parseInt(e.valueOf("second")));
                clock.setTags(e.valueOf("tags"));
                clock.setJson(e.valueOf("json"));
                clock.setBoolean(Boolean.parseBoolean(e.valueOf("switchStatus")));
                Log.i("#text#", e.valueOf("tags") + "=" + e.valueOf("json") + "=" + e.valueOf("switchStatus"));
                list.add(clock);
            }
        }
        return list;
    }

    public static void removeNoteInXML(int id) {
        File file = Utils.getFile(Global.AlarmName);
        if (!selectNoteInClockXML(id)) {
            Log.i("selectNoteInXML",selectNoteInClockXML(id)+"");
            return;
        }
        SAXReader saxReader = new SAXReader(); // 用来读取xml文档
        Document document = null;
        try {
            //url是文件的地址
            document = saxReader.read(file);
            List list = document.selectNodes("//clocks");//查找指定的节点
            Iterator iterator = list.iterator();//迭代citys下所有的节点
            while (iterator.hasNext()) {
                Element ele = (Element) iterator.next();
                Iterator it = ele.elementIterator("clock");//指定到city
                while (it.hasNext()) {
                    Element es = (Element) it.next();//迭代的所有city节点
                    //Log.i("taggg",es.getName() + "#" + es.valueOf("city_en") + "#" + es.getStringValue());
                    if (es.valueOf("_id") != null && id == Integer.parseInt(es.valueOf("_id"))) {
                        ele.remove(es);
                    }
                }
            }
            XMLUtils.Writer(document, file);
        } catch (DocumentException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
