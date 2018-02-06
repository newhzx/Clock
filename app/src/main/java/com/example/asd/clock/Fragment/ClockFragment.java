package com.example.asd.clock.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.asd.clock.Clock.AddClock;
import com.example.asd.clock.Fragment.Adapter.ClockAdapter;
import com.example.asd.clock.Fragment.Base.BaseFragment;
import com.example.asd.clock.Fragment.Bean.Clock;
import com.example.asd.clock.R;
import com.example.asd.clock.Utils.ClockXMLUtils;
import com.example.asd.clock.Utils.Utils;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.example.asd.clock.Utils.ClockXMLUtils.readClock;


//闹钟界面
public class ClockFragment extends BaseFragment {
    private List<Clock> list;//存放当前list内容的对象
    private ClockAdapter adapter;
    private ListView listView;
    private boolean isEdit = false;//是否编辑状态

    public ClockFragment() {
    }

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
        list = readClock();
        adapter.updateListView(list, isEdit);
        ClockAdapter.initData();
    }

    @Override
    public View baseView(View view) throws IOException, DocumentException {
        title.setText("闹钟");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, AddClock.class), 20);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToast(activity, "clock");
                isEdit = !isEdit;//取反
                //设置当前状态
                if (isEdit) {
                    edit.setText("完成");
                } else {
                    edit.setText("编辑");
                }
                mHandler.sendMessage(mHandler.obtainMessage());
            }
        });
        list = readClock();//读取xml文件
        adapter = new ClockAdapter(activity, list, isEdit);//实例化adapter
        listView = new ListView(activity);
        listView.setAdapter(adapter);//设置adapter
        listView.setDividerHeight(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean isDeleteIng = ClockAdapter.initStatus();
                if (isEdit && !isDeleteIng) {
//                    Utils.showToast(activity, position + "");
                    mHandler.sendMessage(mHandler.obtainMessage());
                    Intent it = new Intent(activity, AddClock.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("json", list.get(position));
                    bundle.putBoolean("isEdit", isEdit);
                    bundle.putInt("position", position);
                    it.putExtras(bundle);
                    startActivityForResult(it, 25);
                } else {
                    ClockAdapter.initDeleteButton();
                }

            }
        });

        adapter.setOnItemClickListenerListener(new ClockAdapter.onItemClickListener() {
            @Override
            public void onClick(View view, int position, List<Clock> list) {
                Utils.showToast(activity, "Text" + position);
                View btn_deleteButton_clock = view.findViewById(R.id.btn_deleteButton_clock);
                btn_deleteButton_clock.setOnClickListener(new Click(position, view, list));
                ClockAdapter.initData();
                HashMap<Integer, Boolean> map = ClockAdapter.getIsSelect();
                map.put(position, true);
                ClockAdapter.setIsSelect(map);
                adapter.notifyDataSetChanged();
            }
        });
        mHandler.sendMessage(mHandler.obtainMessage());
        basefragment.addView(listView);//把当前listview添加到内容界面里面
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 20) {
            mHandler.sendMessage(mHandler.obtainMessage());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class Click implements View.OnClickListener {
        int position;
        View view;
        List<Clock> list;

        public Click(int position) {
            this.position = position;
        }

        public Click(int position, View view, List<Clock> list) {
            this(position);
            this.view = view;
            this.list = list;
        }

        @Override
        public void onClick(View v) {
            int removeId = list.get(position).getId();
            list.remove(position);
            removeItem(removeId);
            mHandler.sendMessage(mHandler.obtainMessage());
        }
    }

    private void removeItem(int id) {
        ClockXMLUtils.removeNoteInXML(id);
    }
}