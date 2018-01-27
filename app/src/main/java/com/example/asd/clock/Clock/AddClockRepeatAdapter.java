package com.example.asd.clock.Clock;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.asd.clock.R;
import com.example.asd.clock.Utils.Global;

import java.util.HashMap;
import java.util.List;

public class AddClockRepeatAdapter extends BaseAdapter {
    public static List list;
    private Activity activity;
    private static HashMap<Integer,Boolean> map = null;
    //如果没有初始化，需要手动初始化map
    public AddClockRepeatAdapter(List list, Activity activity) {
        this.list = list;
        this.activity = activity;
        this.map = initMap();
    }
    //已经初始化 直接传参过来 不需要再次初始化
    public AddClockRepeatAdapter(List list, Activity activity,HashMap<Integer, Boolean> map) {
        this.list = list;
        this.activity = activity;
        this.map = map;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_week_repeat, null);
            viewHolder.tv_repeat = (TextView) convertView.findViewById(R.id.tv_week_repeat);
            viewHolder.cb_repeat = (CheckBox) convertView.findViewById(R.id.cb_repeat);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //初始化每个选项
        viewHolder.tv_repeat.setText(list.get(position).toString());
        viewHolder.cb_repeat.setChecked(getSelected().get(position));
        return  convertView;
    }
    //返回map集合
    public static HashMap<Integer,Boolean> getSelected(){
        return map;
    }

    //设置setter adapter
    public static void setSelector(HashMap<Integer,Boolean> map){
        AddClockRepeatAdapter.map = map;
    }

    //初始化map集合,所有选项为false
    public static HashMap<Integer, Boolean> initMap() {
        map = new HashMap<Integer, Boolean>();
        for (int i = 0; i< Global.getWeekRepeat.length; i++){
            map.put(i,false);
        }
        return map;
    }
    //获取重复选项选中状态的数目
    public static int getMapCount(){
        int mapCount = 0;
        for (int x = 0 ;x<map.size();x++) {
            if (map.get(x)) ++mapCount;
        }
        return mapCount;
    }
    //viewholder类
    class ViewHolder {
        TextView tv_repeat;
        CheckBox cb_repeat;
    }
}
