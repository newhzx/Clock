package com.example.asd.clock.WorldClock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asd.clock.R;
import com.example.asd.clock.Utils.Global;
import com.example.asd.clock.Utils.Utils;
import com.example.asd.clock.WorldClock.Sortlist.CharacterParser;
import com.example.asd.clock.WorldClock.Sortlist.ClearEditText;
import com.example.asd.clock.WorldClock.Sortlist.PinyinComparator;
import com.example.asd.clock.WorldClock.Sortlist.SideBar;
import com.example.asd.clock.WorldClock.Sortlist.SortAdapter;
import com.example.asd.clock.WorldClock.Sortlist.SortModel;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AddWorldClock extends AppCompatActivity {
    //存放时区信息的HashMap
    private HashMap<String, String> map = new HashMap<String, String>();
    //这个数组只存放时区中文名，用于列表显示
    private ArrayList<String> list = new ArrayList<String>();
    //这个数组只存放时区英文名，用于列表显示
    private ArrayList<String> listIds = new ArrayList<String>();
    private StringBuffer sb;//获取网络信息的内容
    private File file;//读取sd卡文件
    private InputStream xrps = null;
    private myAdapter name;//字母排序适配器类
    private ListView listView;//排序前listview
    private ListView sortListView;//排序后listview
    private SideBar sideBar;//右侧字母排序表
    //显示字母的TextView
    private SortAdapter adapter;
    private ClearEditText mClearEditText;

     //汉字转换成拼音的类
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    //根据拼音来排列ListView里面的数据类
    private PinyinComparator pinyinComparator;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addworldclock);
        initxml();//生成xml文件
        getdata();//获取数据
        listView = (ListView) findViewById(R.id.lv_addworldclock);//实例化排序前的listview
        name = new myAdapter();//字母排序实例化
        listView.setAdapter(name);//设置adapter
        initViews();//排序过程
    }
    private void initxml() {
            //判断是否需要生成xml文件
            file = Utils.getFile("timezone.xml");
            if (file.exists()) {//判断文件是否存在
                Log.i("tags", "已存在该文件");
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpUtils utils = new HttpUtils();
                    utils.send(HttpRequest.HttpMethod.GET, Global.GLOBAL, new RequestCallBack<String>() {
                        //链接成功
                        @Override
                        public void onSuccess(ResponseInfo responseInfo) {
                            String result = (String) responseInfo.result;
                            Log.i("tags","获取网络数据成功");
                            try {
                                //解析网络数据
                                parseData(result);//解析json数据
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //链接失败
                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Log.i("tags", "错误" + msg);
                            error.printStackTrace();
                        }
                    });
                }
            }).start();
    }
    //解析网络数据
    private void parseData(String result) throws JSONException, IOException {
        sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sb.append("<resource>");
        JSONObject jsonObject = new JSONObject(result);//封装为jsonObject
        JSONObject json = jsonObject.getJSONObject("result");//获取result的内容
        Iterator keyIter = json.keys();//获取所有关键词
        String key;//关键词载体
        String value;//关键词对应值载体
        String contry_en;//国家名英文
        String city_en;//城市名英文
        String contry_cn;//国家名中文
        String city_cn;//城市名中文
        JSONObject jsonobject = null;
        //<timezone id="Asia/Shanghai" name="北京/中国"/>
        while (keyIter.hasNext()) {
            sb.append("<timezone id=\"");
            //Key值
            key = (String) keyIter.next();
            //Key对象的Value值
            value = json.get(key).toString();
            //每一个value也是json数据，格式：
//          {"continents_en":"africa","continents_cn":"非洲",
//          "contry_en":"algeria","contry_cn":"阿尔及利亚",
//          "city_en":"algiers","city_cn":"阿尔及尔"}
            jsonobject = new JSONObject(value);//解析value数据
            contry_en = jsonobject.getString("contry_en");//获取国家英文名
            city_en = jsonobject.getString("city_en");//获取城市英文名
            //生成id
            sb.append(contry_en + "/" + city_en + "\"");
            contry_cn = jsonobject.getString("contry_cn");//获取国家中文名
            city_cn = jsonobject.getString("city_cn");//获取城市英文名
            //生成name
            sb.append(" name=\"" + city_cn + "(" + contry_cn + ")\"");
            sb.append("/>");
            //System.out.println("timezone:"+sb.toString());
        }
        sb.append("</resource>");
        Log.i("tags","解析XML完毕，开始生成XML文件");
        //生成xml文件
        WriteToXML();
    }

    //写入到xml文件
    private void WriteToXML() throws IOException {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("tags","写入xml数据成功");
            getdata();//初始化数据
            initViews();//初始化sortlistview
    }
    //取消添加世界时钟 返回到worldclock界面
    public void disAddWorldClock(View view) {
        finish();
    }

    //初始化sortListview
    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sb_sidrbar);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });
        sortListView = (ListView) findViewById(R.id.lv_addworldclock);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                String name_en = ((SortModel) adapter.getItem(position)).getId();
                Utils.showToast(AddWorldClock.this,name_en);
                Intent intent = new Intent();
                intent.putExtra("name_en", name_en);
                setResult(1, intent);
                finish();
            }
        });
        String[] timezones = list.toArray(new String[list.size()]);
        String[] ids = listIds.toArray(new String[listIds.size()]);
        SourceDateList = filledData(timezones, ids);
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mClearEditText = (ClearEditText) findViewById(R.id.et_searchTextView);
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
//  ListView填充数据
    private List<SortModel> filledData(String[] date, String[] ids) {
        List<SortModel> mSortList = new ArrayList<SortModel>();
        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            sortModel.setId(ids[i]);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }

    //根据输入框中的值来过滤数据并更新ListView
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.toUpperCase().indexOf(
                        filterStr.toString().toUpperCase()) != -1
                        || characterParser.getSelling(name).toUpperCase()
                        .startsWith(filterStr.toString().toUpperCase())) {
                    filterDateList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    //从xml文件获取信息
    public void getdata() {
        try {
            //将上次的数据清空，方便重新搜索
            map.clear();
            list.clear();
            listIds.clear();
            //获取资源文件方法
            /*Resources res = getResources();
            XmlResourceParser xrp = res.getXml(R.xml.worldclockzone);*/
            xrps = readXML();
            if(xrps == null) return;
            Log.i("tags","开始读取xml数据");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(xrps, "UTF-8");
            int eventType = parser.getEventType();
            //判断是否已经到了文件的末尾
            while (eventType != XmlPullParser.END_DOCUMENT) {
                //获取资源文件方法
                /*if (eventType == XmlResourceParser.START_TAG) {
                    String name = parser.getName();
                    if (name.equals("timezone")) {
                    //关键词搜索，如果匹配，那么添加进去如果不匹配就不添加，如果没输入关键词“”,那么默认搜索全部
                        map.put(parser.getAttributeValue(1),
                                parser.getAttributeValue(0));
                        list.add(parser.getAttributeValue(1));
                        Log.i("xrp",parser.getAttributeValue(0));
                        listIds.add(parser.getAttributeValue(0));
                    }
                }*/
                //循环遍历文件 生成list和map
                if (eventType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    if (name.equals("timezone")) {
                        map.put(parser.getAttributeValue(1),
                                parser.getAttributeValue(0));
                        list.add(parser.getAttributeValue(1));
                        listIds.add(parser.getAttributeValue(0));
                    }
                }
                eventType = parser.next();
            }
            Log.i("tags","获取xml数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获取文件读取流
    public InputStream readXML() {
            if(!file.exists()){
                return null;
            }
            FileInputStream fis;
            try {
                fis = new FileInputStream(file);
                return fis;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
    }

    //字母排序适配器类
    public class myAdapter extends BaseAdapter {
        ViewHolder holder;
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int pos, View view, ViewGroup arg2) {
            holder = new ViewHolder();
            if (view == null) {
                view = LayoutInflater.from(AddWorldClock.this).inflate(R.layout.item_zone, null);
                holder.view = (TextView) view.findViewById(R.id.tv_zone);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.view.setText(list.get(pos));

            return view;
        }

        class ViewHolder {
            public TextView view;
        }
    }

}
