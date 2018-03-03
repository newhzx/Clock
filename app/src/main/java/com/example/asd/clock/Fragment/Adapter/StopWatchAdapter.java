package com.example.asd.clock.Fragment.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asd.clock.R;

import java.util.List;

//秒表适配器
public class StopWatchAdapter extends BaseAdapter {
    private Activity activity;//初始化活动
    private List<String> list;//计次读秒
    private List<Long> listMill;//更新当前读秒
    //    初始化list集合
    public StopWatchAdapter(Activity activity, List<String> list) {
        this.activity = activity;
        this.list = list;
    }
    //刷新读秒
    public void updateTime(List<String> list,List<Long> listMill){
        this.list = list;
        this.listMill = listMill;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_stopwatch, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //初始化计次数据
        holder.countTv.setText("计次" + (list.size() - position));
        holder.recordTv.setText(list.get(list.size() - position-1));
        //根据最大读秒和最小读秒设置计次选项颜色
        if (listMill.size() >= 2 ){
            int[] index = MaxAndMinPosition(listMill);
            Log.i("indexs","Max:"+index[0]+"—"+"Min:"+index[1]);
            //当前读秒最大显示红色
            if(index[0] == list.size()-position-1){
                holder.countTv.setTextColor(Color.RED);
                holder.recordTv.setTextColor(Color.RED);
                //当前读秒最小显示绿色
            }else if(index[1] == list.size()-position-1){
                holder.countTv.setTextColor(Color.GREEN);
                holder.recordTv.setTextColor(Color.GREEN);
            }else{//其余显示白色
                holder.countTv.setTextColor(Color.WHITE);
                holder.recordTv.setTextColor(Color.WHITE);
            }

        }
        return convertView;
    }
    //算出所有计次的最大值和最小值的位置
    private int[] MaxAndMinPosition(List<Long> listMill) {
        //记录位置
        int indexMax =0;
        int indexMin =0 ;
        //假设最大最小值的数值
        long max = listMill.get(0);
        long min = listMill.get(0);
        for (int i = 0; i < listMill.size(); i++) {
            //算出当前最小值位置
            if (min > (Long)listMill.get(i)){
                min = (Long)listMill.get(i);
                indexMin = i;
            }
            //算出当前最大值位置
            if (max < (Long)listMill.get(i)){
                max = (Long)listMill.get(i);
                indexMax = i;
            }
        }
        //存放最大最小值位置的int数组
        int[] Position = new int[2];
        Position[0] = indexMax;
        Position[1] = indexMin;
        return Position;
    }
    //   内部类
    class ViewHolder {
        TextView countTv, recordTv;
        public ViewHolder(View convertView) {
            countTv = (TextView) convertView.findViewById(R.id.countTv);
            recordTv = (TextView) convertView.findViewById(R.id.recordTv);
        }
    }

}
