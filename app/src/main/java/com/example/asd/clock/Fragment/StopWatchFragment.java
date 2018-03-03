package com.example.asd.clock.Fragment;


import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asd.clock.Fragment.Adapter.StopWatchAdapter;
import com.example.asd.clock.Fragment.Base.BaseFragment;
import com.example.asd.clock.R;
import com.example.asd.clock.Utils.Global;

import java.util.ArrayList;
import java.util.List;

//秒表界面
public class StopWatchFragment extends BaseFragment implements View.OnClickListener {
    private View views;//实例化views
    private long longmillTime, currentmillTime;//总时长 当前时长
    private StopWatchAdapter adapter;//秒表适配器
    private Button cancel, pause;//取消和暂停按钮
    private ListView lv_stopwatch;//秒表listview
    private TextView tv_fragment,tv_currentTime;//总时长  当前时长文本框
    private List<String> list;//记录计次的list集合
    private List<Long> listMill;//记录计次时长的list集合
    private boolean isPause, isCount, isStart;
    private String countTime, currentTime;//总时长和当前时长的文本框
    private Handler uiHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Global.TICK_WHAT:
                    if (isPause) {
                        addCountTime();
                        updateUI();
                    }
                    /*if (isStart) {
                        updateFirstUI();
                    }*/
                    //再次发送handler 成循环发送 刷新时间
                    uiHandle.sendEmptyMessageDelayed(Global.TICK_WHAT, Global.TIME_TO_SEND);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public View baseView(View view) {
        title.setText("秒表");
        edit.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        views = View.inflate(activity, R.layout.stopwatch, null);//实例化秒表界面
        initViews(views);//初始化控件
        tv_fragment.setText("00:00.00");
        tv_currentTime.setText("当前读秒：00:00.00");
        list = new ArrayList<String>();//实例化计次集合
        listMill = new ArrayList<Long>();//实例化计次时长集合
        basefragment.addView(views);
        return view;
    }
    //初始化控件布局
    private void initViews(View view) {
        tv_fragment = (TextView) view.findViewById(R.id.tv_stopwatch_fragment);
        tv_currentTime = (TextView)view.findViewById(R.id.tv_currentTime);
        lv_stopwatch = (ListView) view.findViewById(R.id.lv_stopwatch_dot);
        cancel = (Button) view.findViewById(R.id.btn_stopwatch_cancel);
        pause = (Button) view.findViewById(R.id.btn_stopwatch_pause);
        pause.setOnClickListener(this);
        cancel.setOnClickListener(this);
        cancel.setClickable(false);
    }
    //按钮实现控制秒表功能
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stopwatch_cancel:
                //复位操作
                if (!isCount) {
                    cancel.setText("计次");
                    cancel.setSelected(false);
                    cancel.setClickable(false);
                    uiHandle.removeMessages(Global.TICK_WHAT);
                    tv_fragment.setText("00:00.00");
                    tv_currentTime.setText("当前读秒：00:00.00");
                    longmillTime = 0;
                    currentmillTime = 0;
                    list.removeAll(list);
                    listMill.removeAll(listMill);
                    isStart = false;
                    adapter.notifyDataSetChanged();
                } else {
                    //计次操作
                    list.add(currentTime);
                    listMill.add(currentmillTime);
                    currentmillTime = 0;
                    if (!isStart){
                        isStart = true;
                        adapter = new StopWatchAdapter(activity, list);//实例化adapter
                        lv_stopwatch.setAdapter(adapter);
                    }
                    adapter.updateTime(list,listMill);
                }
                break;
            case R.id.btn_stopwatch_pause:
                //暂停操作
                if (isPause) {
                    isPause = false;
                    isCount = false;
                    cancel.setText("复位");
                    pause.setText("启动");
                    pause.setSelected(false);
                    pause.setTextColor(Color.parseColor("#ffffff"));
                    uiHandle.removeMessages(Global.TICK_WHAT);
                } else {
                    //启动操作
                    isPause = true;
                    isCount = true;
                    cancel.setText("计次");
                    pause.setText("停止");
                    pause.setSelected(true);
                    pause.setTextColor(Color.parseColor("#ffffff"));
                    startTime();
                    if (!isStart) {
                        currentmillTime = 0;
                    }
                }
                cancel.setClickable(true);
                cancel.setSelected(true);//设置开始底色
                cancel.setTextColor(Color.parseColor("#ffffff"));//
                break;
        }
    }
    //开始读秒
    private void startTime() {
        uiHandle.sendEmptyMessageDelayed(Global.TICK_WHAT, Global.TIME_TO_SEND);
    }
    //刷新总时长和当前时长
    private void updateUI() {
        tv_fragment.setText(countTime);
        tv_currentTime.setText("当前读秒： "+currentTime);
    }

    /*private void updateFirstUI() {
        adapter.update(lv_stopwatch,currentTime);
    }*/
    //记录总时长和当前时长的文本
    public void addCountTime() {
        longmillTime += 10;
        currentmillTime += 10;
        countTime = this.getHour(longmillTime) + this.getMin(longmillTime) + ":" + this.getSec(longmillTime) + "." + this.getLongMill(longmillTime);
        currentTime = this.getHour(currentmillTime) + this.getMin(currentmillTime) + ":" + this.getSec(currentmillTime) + "." + this.getLongMill(currentmillTime);
    }

    //得到时
    public String getHour(Long time) {
        long hour = time / 3600000;
        return hour <= 0 ? "" : hour + ":";
    }

    // 得到分

    public String getMin(Long time) {
        long min = (time / 60000) % 60;
        return min < 10 ? "0" + min : min + "";
    }

    //得到秒
    public String getSec(Long time) {
        long sec = (time / 1000) % 60;
        return sec < 10 ? "0" + sec : sec + "";
    }

         //得到0.01秒
    public String getLongMill(Long time) {
        long longmill = (time / 10) % 100;
        return longmill < 10 ? "0" + longmill : longmill + "";
    }

}
