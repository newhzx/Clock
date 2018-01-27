package com.example.asd.clock.Clock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.example.asd.clock.R;
import com.example.asd.clock.Utils.Global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class AddClockRepeat extends Activity implements OnItemClickListener {
    private ListView lv_repeat;//重复listview
    private Button btn_addclockrepeat;//返回按钮
    private static List<String> list;//集合
    public int currentNum = 0;//当前选项的位置
    private int count = 0;//
    private AddClockRepeatAdapter adapter;//重复的adapter
    private HashMap<Integer, Boolean> map;//记录选中的位置
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addclockrepeat);
        init();//初始化
    }

    private void init() {
        //初始化listview控件
        lv_repeat = (ListView) findViewById(R.id.lv_addclockrepeat);
        //返回按钮
        btn_addclockrepeat = (Button) findViewById(R.id.btn_addclockrepeat);
        //获取重复选项集合
        list = getList();
        //获取map的选项情况
        map = AddClockRepeatAdapter.getSelected();
        //设置adapter
        if (map == null) {
            adapter = new AddClockRepeatAdapter(list, AddClockRepeat.this);
        } else {
            currentNum = AddClockRepeatAdapter.getMapCount();
            adapter = new AddClockRepeatAdapter(list, AddClockRepeat.this, map);
        }
        lv_repeat.setAdapter(adapter);

        btn_addclockrepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String repeat = getRepeatContent();
                Intent intent = new Intent();
                intent.putExtra("repeat", repeat);
                setResult(21, intent);
                finish();
            }
        });
        lv_repeat.setOnItemClickListener(this);
    }

    private String getRepeatContent() {
        HashMap<Integer, Boolean> map = AddClockRepeatAdapter.getSelected();
        StringBuffer sb = new StringBuffer();
        if (currentNum == 0) {
            return "永久";
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AddClockRepeatAdapter.ViewHolder holder = (AddClockRepeatAdapter.ViewHolder) view.getTag();
        holder.cb_repeat.toggle();
        AddClockRepeatAdapter.getSelected().put(position, holder.cb_repeat.isChecked());
        if (holder.cb_repeat.isChecked()) {
            currentNum = ++currentNum;
        } else {
            currentNum = --currentNum;
        }
        Log.i("currentNum", currentNum + "");
    }

    public static List<String> getList(){
        list = new ArrayList<String>();
        for (String str : Global.getWeekRepeat) {
            list.add(str);
        }
        return list;
    }
}
