package com.example.asd.clock.Clock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.asd.clock.R;
import com.example.asd.clock.Utils.Global;
import com.example.asd.clock.Utils.VibratorUtils;

import java.util.ArrayList;
import java.util.List;

public class AddClockVibrator extends Activity implements OnItemClickListener, View.OnClickListener {
    private ListView lv_addclock_vibrator;//振动listview
    private AddClockVibratorAdapter adapter;//振动adapter
    private RelativeLayout rl_addclock_vibrator_none;//按钮无
    private CheckBox checkBox;//选项集合
    private Button btn_addclock_music;//返回按钮
    private Vibrator vibrator;//振动类
    private long[] pattern;//振动模式
    private boolean isVirating;//检测是否在振动
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addclockvibrator);
        initView();//初始化控件
        initData();//初始化布局
        initListener();//初始化事件
    }

    private void initData() {
        //设置adapter
        adapter = new AddClockVibratorAdapter(AddClockVibrator.this);
        lv_addclock_vibrator.setAdapter(adapter);
    }

    private void initListener() {
        //设置侦听事件
        lv_addclock_vibrator.setOnItemClickListener(this);
        btn_addclock_music.setOnClickListener(this);
        rl_addclock_vibrator_none.setOnClickListener(this);
    }
    //初始化布局
    private void initView() {
        lv_addclock_vibrator = (ListView) findViewById(R.id.lv_addclock_vibrator);//振动listview
        checkBox = (CheckBox) findViewById(R.id.cb_vibrator_none);//显示按钮无是否选中
        btn_addclock_music = (Button) findViewById(R.id.btn_addclock_music);//返回按钮
        rl_addclock_vibrator_none = (RelativeLayout) findViewById(R.id.rl_addclock_vibrator_none);//按钮无
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击当前选项 停止振动
        if(AddClockVibratorAdapter.vibrator == position){
            if (isVirating) {
                isVirating = false;
                VibratorUtils.virateCancle(AddClockVibrator.this);
                return;
            }
        }
//        设置当前选中状态 并刷新页面
        AddClockVibratorAdapter.vibrator = position;
        adapter.notifyDataSetChanged();
        checkBox.setChecked(false);
        isVirating = true;
        //按照指定的模式去震动。
        //数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
        //第二个参数为重复次数，-1为不重复，0为一直震动
        pattern = getVibratorList().get(position);
        //震动重复，从数组的0开始（-1表示不重复）
        VibratorUtils.vibrate(AddClockVibrator.this, pattern, 0);
    }
    //返回振动名称集合
    public static List<long[]> getVibratorList() {
        List<long[]> list = new ArrayList<>();
        long[][] patterns = Global.getVibratorList;
        for (int i=0;i<patterns.length;i++){
            list.add(i,patterns[i]);
        }
        return list;
    }
    //返回振动名称
    public String getVibratorType() {
        String vibratorType;
        if (AddClockVibratorAdapter.vibrator == -1)
            vibratorType = "无";
        else if (AddClockVibratorAdapter.vibrator == 0){
            vibratorType = "默认";
        }else{
            vibratorType = AddClockVibratorAdapter.getListVibrator().get(AddClockVibratorAdapter.vibrator);
        }
        return vibratorType;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击按钮无 初始化并停止振动
            case R.id.rl_addclock_vibrator_none:
                AddClockVibratorAdapter.vibrator = -1;
                adapter.notifyDataSetChanged();
                checkBox.setChecked(true);
                    if (isVirating) {
                        isVirating = false;
                        VibratorUtils.virateCancle(AddClockVibrator.this);
                    }
                break;
            case R.id.btn_addclock_music:
                VibratorType();//返回按钮 振动模式名称
                break;
        }
    }
    //后退虚拟键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
            VibratorType();//返回按钮 振动模式名称
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {

        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
        }
        return super.onKeyDown(keyCode, event);
    }

    //返回振动类型 返回类型
    private void VibratorType() {
        if (isVirating) {
            isVirating = false;//是否在振动
            VibratorUtils.virateCancle(AddClockVibrator.this);
        }
        String vibrator = getVibratorType();
        Intent intent = new Intent();
        intent.putExtra("vibrator", vibrator);
        setResult(24, intent);
        finish();
    }
}
