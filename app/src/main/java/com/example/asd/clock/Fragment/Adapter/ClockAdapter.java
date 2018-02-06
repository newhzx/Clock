package com.example.asd.clock.Fragment.Adapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asd.clock.Clock.SwitchButton.SwitchButton;
import com.example.asd.clock.Fragment.Bean.Clock;
import com.example.asd.clock.R;
import com.example.asd.clock.Utils.ClockXMLUtils;
import com.example.asd.clock.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//世界时钟适配器
public class ClockAdapter extends BaseAdapter {
    //  初始化界面
    private Activity activity;
    private AlarmManager alarmManager;
    private static List<Clock> list;
    private boolean isEdit = false;
    public static ViewGroup parent;
    private RelativeLayout.LayoutParams lp;
    public static HashMap<Integer,Boolean> isSelect = new HashMap<Integer,Boolean>();
    public ClockAdapter(Activity activity, List<Clock> list,boolean isEdit) {
        this.activity = activity;
        this.list = list;
        this.isEdit = isEdit;
        initData();
    }

    public static void initData() {
        isSelect.clear();
        for (int i = 0 ; i <list.size();i++){
            isSelect.put(i,false);
            setIsSelect(isSelect);
        }
    }

    //刷新listview
    public void updateListView(List<Clock> list, boolean isEdit) {
        this.list = list;
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }

    public static HashMap<Integer,Boolean> getIsSelect(){
        return isSelect;
    }

    public static void setIsSelect(HashMap<Integer, Boolean> isSelect) {
        ClockAdapter.isSelect = isSelect;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Clock getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        this.parent = parent;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            //该item的实例化  不是编辑状态下的四个文本框
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_clock, null);
            viewHolder.tv_lunch_clock = (TextView) convertView.findViewById(R.id.tv_lunch_clock);
            viewHolder.tv_clocktime_clock = (TextView) convertView.findViewById(R.id.tv_clocktime_clock);
            viewHolder.tv_tagAndjson_clock = (TextView) convertView.findViewById(R.id.tv_tagAndjson_clock);

            //编辑状态需要用到的控件内容  实例化并存在viewholder中
            viewHolder.rl_item_clock = (RelativeLayout) convertView.findViewById(R.id.rl_item_clock);
            viewHolder.rl_delete_item_clock = (RelativeLayout) convertView.findViewById(R.id.rl_delete_item_clock);
            viewHolder.rl_content_clock = (RelativeLayout) convertView.findViewById(R.id.rl_content_clock);
            viewHolder.ll_content_clock = (LinearLayout) convertView.findViewById(R.id.ll_content_clock);
            viewHolder.ll_content_item_clock = (LinearLayout) convertView.findViewById(R.id.ll_content_item_clock);
            viewHolder.sb_clock = (SwitchButton) convertView.findViewById(R.id.sb_clock);
            viewHolder.tv_row_clock = (TextView) convertView.findViewById(R.id.tv_row_clock);
            viewHolder.btn_deleteButton_clock = (Button) convertView.findViewById(R.id.btn_deleteButton_clock);
            viewHolder.iv_delete_item_clock = (ImageView) convertView.findViewById(R.id.iv_delete_item_clock);
            //把viewholder存入到convertview中
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取当前选中的item对象
        Clock clock = getItem(position);
        //显示上下午
        if (clock.getLunchSelect() == 0) {
            viewHolder.tv_lunch_clock.setText("上午");
        } else if (clock.getLunchSelect() == 1) {
            viewHolder.tv_lunch_clock.setText("下午");
        }
        //显示闹钟时间信息
        int hourSelect = clock.getHourSelect();
        int minuteSelect = clock.getMinuteSelect();
        String minute = minuteSelect + "";
        if (hourSelect == 0) hourSelect = 12;
        if (minuteSelect < 10) minute = "0" + minuteSelect;
        viewHolder.tv_clocktime_clock.setText(hourSelect + ":" + minute);
        //备注以及周期
        String tag = clock.getTags();
        String json = clock.getJson();
        Gson gson = new Gson();
        Map<Integer, Boolean> maps = gson.fromJson(json, new TypeToken<HashMap<Integer, Boolean>>() {
        }.getType());
        String repeat = Utils.getRepeatContent(maps);
        if ("".equals(repeat)) {
            viewHolder.tv_tagAndjson_clock.setText(tag);
        } else {
            viewHolder.tv_tagAndjson_clock.setText(tag + ",  " + repeat);
        }
         lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        if (isEdit) {
            //编辑状态需要显示的控件
            viewHolder.iv_delete_item_clock.setVisibility(View.VISIBLE);
            viewHolder.tv_row_clock.setVisibility(View.VISIBLE);
            viewHolder.rl_delete_item_clock.setVisibility(View.VISIBLE);

            lp.setMargins(120, 0, 10, 0);
            viewHolder.rl_content_clock.setLayoutParams(lp);
            viewHolder.iv_delete_item_clock.setMinimumHeight(viewHolder.rl_item_clock.getHeight());
            //编辑状态需要隐藏的按钮
            viewHolder.sb_clock.setVisibility(View.GONE);
        } else {
            initStatus();
            //非编辑状态 隐藏控件内容
            viewHolder.ll_content_clock.setPadding(0, 0, 0, 0);
            viewHolder.tv_row_clock.setVisibility(View.GONE);
            viewHolder.iv_delete_item_clock.setVisibility(View.GONE);
            viewHolder.rl_delete_item_clock.setVisibility(View.GONE);

            lp.setMargins(40, 0, 10, 0);
            viewHolder.rl_content_clock.setLayoutParams(lp);

            // 非编辑状态 显示切换按钮控件
            viewHolder.sb_clock.setVisibility(View.VISIBLE);
        }
        if(clock.getBoolean()){
            viewHolder.tv_lunch_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            viewHolder.tv_clocktime_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            viewHolder.tv_tagAndjson_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            viewHolder.sb_clock.setChecked(true);
        }else{
            viewHolder.tv_lunch_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
            viewHolder.tv_clocktime_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
            viewHolder.tv_tagAndjson_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
            viewHolder.sb_clock.setChecked(false);
        }

        //switchButton 添加侦听事件
        viewHolder.sb_clock.setOnCheckedChangeListener(new OnCheckChange(viewHolder,position));


        final View finalConvertView = convertView;
        viewHolder.rl_delete_item_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(finalConvertView,position,list);
            }
        });
        if(getIsSelect().get(position)){
            viewHolder.rl_item_clock.setPadding(-120, viewHolder.rl_item_clock.getPaddingTop(), 0, viewHolder.rl_item_clock.getPaddingBottom());
            viewHolder.tv_row_clock.setPadding(0, 0, viewHolder.btn_deleteButton_clock.getWidth(), 0);
            //弹出删除按钮
            viewHolder.btn_deleteButton_clock.setPadding(0, 0, 0, 0);
            viewHolder.btn_deleteButton_clock.setVisibility(View.VISIBLE);
            viewHolder.btn_deleteButton_clock.setMinimumHeight(viewHolder.rl_item_clock.getHeight());
        }else{
            viewHolder.rl_item_clock.setPadding(0, 0, 0, 0);
            viewHolder.tv_row_clock.setPadding(0, 0, 0, 0);
            //退出删除按钮
            viewHolder.btn_deleteButton_clock.setPadding(0, 0, 0, 0);
            viewHolder.btn_deleteButton_clock.setVisibility(View.GONE);
        }
        return convertView;
    }

    //  内部类
    class ViewHolder {
        RelativeLayout rl_item_clock;
        RelativeLayout rl_delete_item_clock;
        RelativeLayout rl_content_clock;
        LinearLayout ll_content_clock;
        LinearLayout ll_content_item_clock;
        TextView tv_lunch_clock;
        TextView tv_clocktime_clock;
        TextView tv_tagAndjson_clock;
        TextView tv_row_clock;
        SwitchButton sb_clock;
        ImageView iv_delete_item_clock;
        Button btn_deleteButton_clock;
    }

    private class OnCheckChange implements CompoundButton.OnCheckedChangeListener {
        ViewHolder viewHolder;
        int position;
        public OnCheckChange(ViewHolder viewHolder,int position) {
            this.viewHolder = viewHolder;
            this.position = position;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                viewHolder.tv_lunch_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                viewHolder.tv_clocktime_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                viewHolder.tv_tagAndjson_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));

                alarmManager = (AlarmManager)getSystemService(activity.ALARM_SERVICE);
                Intent intent = new Intent(TAG);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//SDK API >= 23
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//SDK API >= 19 && SDK API < 23
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
                } else {//SDK API < 19
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), TIME_INTERVAL, pendingIntent);
                }

            } else {
                viewHolder.tv_lunch_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
                viewHolder.tv_clocktime_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
                viewHolder.tv_tagAndjson_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
            }
            try {
                ClockXMLUtils.modifyNodeInClockXML(list.get(position).getId(),isChecked);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
    }

    public interface onItemClickListener {
        void onClick(View view, int position, List<Clock> list);
    }
    private onItemClickListener mOnItemClickListener;

    public void setOnItemClickListenerListener(onItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public static boolean initStatus() {
        for (int i =0 ;i<parent.getChildCount();i++){
            View view = parent.getChildAt(i);
            Button btn = view.findViewById(R.id.btn_deleteButton_clock);
            RelativeLayout real = view.findViewById(R.id.rl_item_clock);
            TextView tv_row = view.findViewById(R.id.tv_row_clock);
            int ids = btn.getVisibility();

            btn.setVisibility(View.GONE);
            real.setPadding(0, 0, 0, 0);
            tv_row.setPadding(0, 0, 0, 0);
            if(ids == 0){
                return true;
            }
        }
        return false;
    }

    public static void initDeleteButton() {
        for (int i =0 ;i<parent.getChildCount();i++){
            View view = parent.getChildAt(i);
            Button btn = view.findViewById(R.id.btn_deleteButton_clock);
            RelativeLayout real = view.findViewById(R.id.rl_item_clock);
            TextView tv_row = view.findViewById(R.id.tv_row_clock);

            btn.setVisibility(View.GONE);
            real.setPadding(0, 0, 0, 0);
            tv_row.setPadding(0, 0, 0, 0);
        }
    }
}
