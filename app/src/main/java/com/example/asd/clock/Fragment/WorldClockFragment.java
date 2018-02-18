package com.example.asd.clock.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.asd.clock.Fragment.Adapter.WorldClockAdapter;
import com.example.asd.clock.Fragment.Base.BaseFragment;
import com.example.asd.clock.Fragment.Bean.WorldClock;
import com.example.asd.clock.R;
import com.example.asd.clock.Utils.Global;
import com.example.asd.clock.Utils.Utils;
import com.example.asd.clock.Utils.XMLUtils;
import com.example.asd.clock.WorldClock.AddWorldClock;
import com.example.asd.clock.WorldClock.DragAndDrapListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.dom4j.DocumentException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.example.asd.clock.Utils.XMLUtils.readWorldClock;
import static java.lang.Integer.parseInt;
//世界时钟界面
@SuppressLint("ValidFragment")
public class WorldClockFragment extends BaseFragment{
    private File file;//需要生成的文件
    private Context context;//context内容
    private boolean isEdit = false;//是否编辑状态
    private List<WorldClock> list;//存放当前list内容的对象
    private Activity activity;//存入的activity 用来初始化item界面
    private WorldClockAdapter adapter;//适配器类
    private DragAndDrapListView dlv_workclock;//自定义listview 可实现拖拉效果
    //刷新页面的handler
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                refreshUI();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    };
    //刷新界面方法
    private void refreshUI() throws IOException, DocumentException {
        adapter.updateListView(isEdit);
    }
    //线程类
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    mHandler.sendMessage(mHandler.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public WorldClockFragment() {
        super();
    }
    public WorldClockFragment(Activity activity) {
        super();
        this.activity = activity;
    }
    //抽象方法 必须实现的内容
    @Override
    public View baseView(View view) throws DocumentException, IOException {
        title.setText("世界时钟");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, AddWorldClock.class), 1);
            }
        });
        list = readWorldClock();//读取xml文件
        adapter = new WorldClockAdapter(activity, list);//实例化adapter
        dlv_workclock = new DragAndDrapListView(activity);
        dlv_workclock.setAdapter(adapter);//设置adapter
        dlv_workclock.setDividerHeight(0);
        dlv_workclock.setDragViewId(R.id.ll_threeview);//需要实现的拖拉id
        //保存当前list集合
        Utils.setLists(list);
        //编辑按钮
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(activity,"worldclock");
                isEdit = !isEdit;//取反
                //设置当前状态
                dlv_workclock.setIsDragAndDrop(isEdit);
                if (isEdit) {
                    edit.setText("完成");
                } else {
                    edit.setText("编辑");
                    WorldClockAdapter.initData();
                    if (Utils.getList() != null) {
                        try {
                            //重写list集合 实现重新排序效果
                            XMLUtils.overwriteNotesToXml(Utils.getList());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.i("isEdit", String.valueOf(isEdit));
            }
        });

        dlv_workclock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WorldClockAdapter.initData();
                WorldClockAdapter.initDeleteButton();
            }
        });

        adapter.setOnItemClicksListener(new WorldClockAdapter.onItemClicksListener() {
            public void onClicks(View view, int position, List<WorldClock> list) {
                View btn_delete = view.findViewById(R.id.btn_delete);
                btn_delete.setOnClickListener(new Click(position, view, list));
                WorldClockAdapter.initData();
                HashMap<Integer,Boolean> map = WorldClockAdapter.getCitySelect();
                map.put(position, true);
                WorldClockAdapter.setCitySelect(map);
                adapter.notifyDataSetChanged();
            }
        });
        new Thread(mRunnable).start();//启动线程
        basefragment.addView(dlv_workclock);//把当前listview添加到内容界面里面
        return view;
    }

    private class Click implements View.OnClickListener {
        int position;
        View view;
        List<WorldClock> list;

        public Click(int position) {
            this.position = position;
        }

        public Click(int position, View view, List<WorldClock> list) {
            this(position);
            this.view = view;
            this.list = list;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_threeview:
                    Toast.makeText(activity,"text",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_delete:
                    String removeName = list.get(position).getCity_en();
                    list.remove(position);
                    removeItem(removeName);
                    WorldClockAdapter.initData();
                    mHandler.sendMessage(mHandler.obtainMessage());
                    break;
            }
        }
    }

    private void removeItem(String city_en) {
        XMLUtils.removeNoteToXML(city_en);
    }

    //startActivityForResult回调方法
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            //分割字符串
            String name_en = data.getStringExtra("name_en");
            String city_en = parseCityName(name_en);
            String country_en = parseCountryName(name_en);
            Log.i("Activity", city_en + ":" + country_en);
            getCityTime(city_en);//解析数据
            mHandler.sendMessage(mHandler.obtainMessage());
        }
    }

    private void getCityTime(String city_en) {
        //拼接url 获取该城市时间
        String URL = Global.BEGIN + city_en + Global.END;
        HttpUtils utils = new HttpUtils();//HttpUtils工具
        //获取该城市回调信息
        utils.send(HttpRequest.HttpMethod.GET, URL, new RequestCallBack<String>() {
            //链接成功
            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                String result = (String) responseInfo.result;
                Log.i("tags", "获取该城市网络数据成功");
                Log.i("tags", result);
                try {
                    parseTime(result);//解析返回结果
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
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

    //解析数据
    private void parseTime(String result) throws JSONException, IOException, DocumentException {
        JSONObject jsonObject = new JSONObject(result);//将字符串转化为json对象
        JSONObject json = jsonObject.getJSONObject("result");//获取result下面的内容
        String city_en = json.get("city_en").toString();
        String city_cn = json.get("city_cn").toString();
        String timestamp = json.get("timestamp").toString();
        String bjt_timestamp = json.get("bjt_timestamp").toString();
        //计算时间差
        int distimestamp = parseInt(timestamp) - parseInt(bjt_timestamp);
        int hours = distimestamp / 3600;
        String hour;
        Log.i("hours", String.valueOf(hours));
        if (hours >= 0) hour = "+" + hours;
        else hour = "" + hours;
        //添加到xml文件中
        XMLUtils.addNoteToXML(city_en, city_cn, hour);
        //刷新列表
        adapter.updateListView(readWorldClock(), isEdit);
    }

    //获取国家名
    private String parseCountryName(String name_en) {
        String[] strs = parse(name_en);
        return strs[0];
    }

    //获取城市名
    private String parseCityName(String name_en) {
        String[] strs = parse(name_en);
        return strs[1];
    }

    //分割字符串
    private String[] parse(String name) {
        return name.split("/");
    }
}
