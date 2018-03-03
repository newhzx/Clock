package com.example.asd.clock.Utils;

import android.util.Log;

import com.example.asd.clock.Fragment.Bean.WorldClock;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//世界时钟读写XML工具类 需要使用dom4j jar包
public class XMLUtils {
    //写入更新到xml文件中
    public static void Writer(Document doc, File file) throws IOException {
        //文件写入对象
        FileOutputStream fos = new FileOutputStream(file);
        //写入对象的格式
        OutputStreamWriter osw = new OutputStreamWriter(fos, Global.ENCRYPT);
        //写入格式对象
        OutputFormat of = new OutputFormat();
        of.setEncoding(Global.ENCRYPT);
        of.setIndent(true);
        of.setNewlines(true);
        //xml写入工具类
        XMLWriter writer = new XMLWriter(osw, of);
        writer.write(doc);//把document对象通过xml写入工具类对象写入到xml文件中
        writer.close();//关闭写入工具类对象
    }
    //读取指定文件的节点 返回list集合
    public static List<WorldClock> readWorldClock() throws DocumentException, IOException {
        File file = Utils.isNotExistCreateFile(Global.CityTime,"citys");//判断是否存在该xml文件
        SAXReader sax = new SAXReader();//创建一个SAXReader对象
        Document document = sax.read(file);//获取document对象,如果文档无节点，则会抛出Exception提前结束
        Element root = document.getRootElement();//获取根节点
        List<WorldClock> list = getNodes(root);//从根节点开始遍历所有节点
        return list;//返回list集合
    }
    //获取指定xml文件的所有节点并返回list集合
    public static List<WorldClock> getNodes(Element root) {
        List<WorldClock> list = new ArrayList<>();//存放节点的list集合
        List nodes = root.selectNodes("/citys");//查找指定的节点
        Iterator iterator = nodes.iterator();//迭代citys下所有的节点
        while (iterator.hasNext()) {//迭代根节点
            Element ele = (Element) iterator.next();//指向下一个根节点
            Iterator it = ele.elementIterator("city");//指定到city
            WorldClock wc = null;
            while (it.hasNext()) {
                wc = new WorldClock();//生成世界时间对象
                Element e = (Element) it.next();//迭代的所有city节点
                //设置对象存放节点内容
                wc.setCity_en(e.valueOf("city_en"));
                wc.setCity_cn(e.valueOf("city_cn"));
                wc.setHours(e.valueOf("hours"));
                Log.i("#text#", e.valueOf("city_en") + "=" + e.valueOf("city_cn") + "=" + e.valueOf("hours"));
                list.add(wc);//返回list集合
            }
        }
        return list;
    }
    //删除指定节点内容的 操作xml文件
    public static void removeNoteToXML(String city_en) {
        File file = Utils.getFile(Global.CityTime);//判断该文件是否存在
        if (!selectNoteInXML(city_en)) {//该城市是否在xml文件中存在
            Log.i("selectNoteInXML",selectNoteInXML(city_en)+"");
            return;
        }
        SAXReader saxReader = new SAXReader(); // 用来读取xml文档
        Document document = null;
        try {
            //url是文件的地址
            document = saxReader.read(file);
            List list = document.selectNodes("/citys");//查找指定的节点
            Iterator iterator = list.iterator();//迭代citys下所有的节点
            while (iterator.hasNext()) {//是否有下一个根节点
                Element ele = (Element) iterator.next();//指向下一个根节点
                Iterator it = ele.elementIterator("city");//指定到city
                while (it.hasNext()) {//是否有下一个子节点
                    Element es = (Element) it.next();//迭代的所有city节点
                    //Log.i("taggg",es.getName() + "#" + es.valueOf("city_en") + "#" + es.getStringValue());
                    if (es.valueOf("city_en") != null && city_en.equals(es.valueOf("city_en"))) {
                        ele.remove(es);
                    }
                }
            }
            //写入到xml文件中
            Writer(document, file);
        } catch (DocumentException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //判断xml文件是否存在某节点
    public static boolean selectNoteInXML(String city_en) {
        File file = Utils.getFile(Global.CityTime);//是否存在该节点
        Document document = null;//生成document对象
        SAXReader saxReader = new SAXReader(); // 用来读取xml文档
        try {
            document = saxReader.read(file); //url是文件的地址
            List list = document.selectNodes("/citys");//查找指定的节点
            Iterator iterator = list.iterator();//迭代citys下所有的节点
            while (iterator.hasNext()) {//是否有下一个根节点
                Element ele = (Element) iterator.next();//指向下一个节点
                Iterator it = ele.elementIterator("city");//指定到city
                while (it.hasNext()) {//是否有下一个子节点
                    Element es = (Element) it.next();//迭代的所有city节点
                    //Log.i("taggg",es.getName() + "#" + es.valueOf("city_en") + "#" + es.getStringValue());
                    if (es.valueOf("city_en") != null && city_en.equals(es.valueOf("city_en"))) {
                        return true;
                    }
                }
            }
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
        return false;
    }
    //添加节点到xml文件中
    public static void addNoteToXML(String city_en, String city_cn, String hours) throws IOException, DocumentException {
        File file = Utils.isNotExistCreateFile(Global.CityTime,"citys");//是否存在该xml文件，不存在就创建
        //判断该节点是否存在于xml中
        if (selectNoteInXML(city_en)) {//是否存在该节点
            Log.i("selectNoteInXML",selectNoteInXML(city_en)+"");
            return;
        }
        //dom4j的内容
        SAXReader reader = new SAXReader();
        Document doc = reader.read(new FileInputStream(file));//生成读取文件对象
        Element root = doc.getRootElement();//获取该文件对象的根节点
        Element e1 = root.addElement("city");//添加子节点
        e1.addElement("city_en").addText(city_en);//子节点内容
        e1.addElement("city_cn").addText(city_cn);//子节点内容
        e1.addElement("hours").addText(hours);//子节点内容
        Writer(doc, file);//写入到xml文件中
        Log.i("isFileExist", "成功添加Element数据");
    }
    //重写xml文件  重新排列节点的顺序
    public static void overwriteNotesToXml(List<WorldClock> list) throws IOException, DocumentException {
        File file = Utils.isNotExistCreateFile(Global.CityTime,"citys");//是否存在该xml文件
        SAXReader reader = new SAXReader(); //dom4j的内容
        Document doc = reader.read(new FileInputStream(file));//生成读取文件对象
        //首先清空xml的所有city节点 然后重新写入
        doc = removeAllNotesToXml(doc);
        Element root = doc.getRootElement();//获取该文件对象的根节点
        for (WorldClock wc:list) {//list集合重新写入到xml文件中
            Element e1 = root.addElement("city");//添加子节点
            e1.addElement("city_en").addText(wc.getCity_en());//子节点内容
            e1.addElement("city_cn").addText(wc.getCity_cn());//子节点内容
            e1.addElement("hours").addText(wc.getHours());//子节点内容
        }
        Writer(doc, file);//写入到文件中
        Log.i("isFileExist", "已经成功添加Element数据");
    }
    //删除xml文件的所有city节点
    private static Document removeAllNotesToXml(Document doc) {
            List list = doc.selectNodes("/citys");//查找指定的节点
            Iterator iterator = list.iterator();//迭代citys下所有的节点
            while (iterator.hasNext()) {//是否有下一个根节点
                Element ele = (Element) iterator.next();//指向下一个根节点
                Iterator it = ele.elementIterator("city");//指定到city
                while (it.hasNext()) {
                    Element es = (Element) it.next();//迭代的所有city节点
                    Log.i("taggg",es.getName() + "#" + es.valueOf("city_en") + "#" + es.getStringValue());
                    ele.remove(es);//删除该子节点
                }
            }
            return doc;
    }
}
