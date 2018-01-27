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

import java.util.ArrayList;
import java.util.List;
class AddClockVibratorAdapter extends BaseAdapter{
    private Activity activity;
    public static  List<String> listVibrator;//振动类型集合
    public static int vibrator = 0;//存储当前选择振动的位置

    public AddClockVibratorAdapter(Activity activity){
        this.activity = activity;//实例化activity
        this.listVibrator = getListVibrator();//获取振动集合
    }
    @Override
    public int getCount() {
        return listVibrator.size();
    }

    @Override
    public Object getItem(int position) {
        return listVibrator.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler viewHodler = null;
        if (convertView == null) {
            viewHodler = new ViewHodler();
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_vibrator, null);
            viewHodler.tv_vibrator = (TextView) convertView.findViewById(R.id.tv_vibrator);
            viewHodler.cb_vibrator = (CheckBox) convertView.findViewById(R.id.cb_vibrator);
            convertView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHodler) convertView.getTag();
        }
        //设置振动选项
        String str = getItem(position).toString();
        viewHodler.tv_vibrator.setText(str);
        //设置振动选中状态
        if(vibrator == position){
            viewHodler.cb_vibrator.setChecked(true);
        }else{
            viewHodler.cb_vibrator.setChecked(false);
        }
        return convertView;
    }

    //adapter viewholder
    class ViewHodler{
        CheckBox cb_vibrator;
        TextView tv_vibrator;
    }

    //获取振动list集合
    public static List<String> getListVibrator() {
        listVibrator = new ArrayList<String>();
        String[] strs = Global.getVibrator;
        for (int i = 0; i < strs.length; i++) {
            listVibrator.add(strs[i]);
        }
        return listVibrator;
    }
}
