package com.example.asd.clock.Fragment.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
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

import com.example.asd.clock.Clock.AlarmClockReceiver;
import com.example.asd.clock.Clock.SwitchButton.SwitchButton;
import com.example.asd.clock.Fragment.Bean.Clock;
import com.example.asd.clock.R;
import com.example.asd.clock.Utils.ClockXMLUtils;
import com.example.asd.clock.Utils.Global;
import com.example.asd.clock.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

//世界时钟适配器
public class ClockAdapter extends BaseAdapter {
    //  初始化界面
    public static Activity activity;
    private AlarmManager alarmManager;
    public static Context context;
    private static List<Clock> list;
    public static List<Boolean> listChoose = new ArrayList<>();
    private boolean isEdit = false;
    public static ViewGroup parent;
    private RelativeLayout.LayoutParams lp;
    private static final String TAG = "AlarmClockReceiver";
    public static HashMap<Integer, Boolean> isSelect = new HashMap<Integer, Boolean>();
    public static HashMap<Integer, Boolean> isChoose = new HashMap<Integer, Boolean>();
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
                notifyDataSetChanged();
        }
    };
    public ClockAdapter(){};
    public ClockAdapter(Activity activity, List<Clock> list, boolean isEdit, Context context) throws IOException, DocumentException {
        this.activity = activity;
        this.list = list;
        this.isEdit = isEdit;
        this.context = context;
        initData();
        initSwitch();
    }

    public static void correctIdBool() throws IOException, DocumentException {
        for (int i =0;i<isChoose.size();i++){
            ClockXMLUtils.modifyNodeInClockXML(list.get(i).getId(),listChoose.get(i));
        }
    }

    public static void initSwitch() throws IOException, DocumentException {
        Log.i("chooseList","chooseList1");
        SharedPreferences sp = activity.getSharedPreferences("chooseList", MODE_PRIVATE);
        Log.i("chooseList","chooseList2");
        String chooseList = sp.getString("choose","");
        Log.i("chooseList","chooseList3");
        Log.i("chooseList",chooseList);
        if(chooseList!=null&&!"".equals(chooseList)){
            listChoose = String2List(chooseList);
            for (int i = 0; i < listChoose.size(); i++) {
                isChoose.put(i,listChoose.get(i));
            }
        }else{
            for (int i = 0; i < list.size(); i++) {
                isChoose.put(i,list.get(i).getBoolean());
                listChoose.add(i,list.get(i).getBoolean());
            }
        }
        setChoose(isChoose);
    }

    public static List<Boolean> String2List(String chooseList) {
        String[] chooseLists = chooseList.split(",");
        for(int i=0;i<chooseLists.length;i++){
            listChoose.add(Boolean.parseBoolean(chooseLists[i]));
        }
        return listChoose;
    }

    public static void List2String(List<Boolean> listChoose,Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences("chooseList", Context.MODE_PRIVATE);
        //得到SharedPreferences.Editor对象，并保存数据到该对象中
        SharedPreferences.Editor editor = sp.edit();
        StringBuffer sb = new StringBuffer();
        for (int i = 0 ;i <listChoose.size();i++){
            sb.append(listChoose.get(i)+",");
        }
        editor.putString("choose",sb.toString());
        //保存key-value对到文件中
        editor.commit();
    }

    public  HashMap<Integer, Boolean> initSwitch(List<Boolean> list) throws IOException, DocumentException {
        for (int i = 0; i < list.size(); i++) {
            isChoose.put(i,list.get(i));
        }
        mHandler.sendMessage(mHandler.obtainMessage());
        return isChoose;
    }

    public static void initData() {
        isSelect.clear();
        for (int i = 0; i < list.size(); i++) {
            isSelect.put(i, false);
        }
    }
    //刷新listview
    public void updateListView(List<Clock> list, boolean isEdit) {
        this.list = list;
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }

    public static HashMap<Integer, Boolean> getIsSelect() {
        return isSelect;
    }

    public static void setIsSelect(HashMap<Integer, Boolean> isSelect) {
        ClockAdapter.isSelect = isSelect;
    }

    public static HashMap<Integer, Boolean> getChoose() {
        return isChoose;
    }

    public static void setChoose(HashMap<Integer, Boolean> isChoose) {
        ClockAdapter.isChoose = isChoose;
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
        //switchButton 添加侦听事件
        viewHolder.sb_clock.setOnCheckedChangeListener(new OnCheckChange(viewHolder, position));

        if (getChoose().get(position)) {
            viewHolder.tv_lunch_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            viewHolder.tv_clocktime_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            viewHolder.tv_tagAndjson_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            viewHolder.sb_clock.setChecked(true);
        } else {
            viewHolder.tv_lunch_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
            viewHolder.tv_clocktime_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
            viewHolder.tv_tagAndjson_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
            viewHolder.sb_clock.setChecked(false);
        }
        final View finalConvertView = convertView;
        viewHolder.rl_delete_item_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(finalConvertView, position, list);
            }
        });
        if (getIsSelect().get(position)) {
            viewHolder.rl_item_clock.setPadding(-120, viewHolder.rl_item_clock.getPaddingTop(), 0, viewHolder.rl_item_clock.getPaddingBottom());
            viewHolder.tv_row_clock.setPadding(0, 0, viewHolder.btn_deleteButton_clock.getWidth(), 0);
            //弹出删除按钮
            viewHolder.btn_deleteButton_clock.setPadding(0, 0, 0, 0);
            viewHolder.btn_deleteButton_clock.setVisibility(View.VISIBLE);
            viewHolder.btn_deleteButton_clock.setMinimumHeight(viewHolder.rl_item_clock.getHeight());
        } else {
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

    class OnCheckChange implements CompoundButton.OnCheckedChangeListener {
        ViewHolder viewHolder;
        int position;
        int hourOfDay;
        int minute;
        int second;
        Clock clock;

        public OnCheckChange(ViewHolder viewHolder, int position) {
            this.viewHolder = viewHolder;
            this.position = position;
            initCanlendar();
        }

        private void initCanlendar() {
            clock = list.get(position);
            if (clock.getLunchSelect() == 0) {
                hourOfDay = clock.getHourSelect();
            } else if (clock.getLunchSelect() == 1) {
                hourOfDay = clock.getHourSelect() + 12;
            }
            minute = clock.getMinuteSelect();
            second = clock.getSecond();
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isClickable()) {
                return;
            }
            if (isChecked) {
                viewHolder.tv_lunch_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                viewHolder.tv_clocktime_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                viewHolder.tv_tagAndjson_clock.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                isChoose.put(position,true);
                listChoose.set(position,true);
                if(alarmManager==null){
                    alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                }
                Intent intent = new Intent(activity,AlarmClockReceiver.class);
                intent.putExtra("clock",clock);
                intent.putExtra("position",position);
                intent.setAction(Global.Action+"."+clock.getId());//设置action 区别别的广播
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                long firstTime = SystemClock.elapsedRealtime();
                long systemTime = System.currentTimeMillis();//系统时间
                Calendar c = Calendar.getInstance();
                // 根据用户选择时间来设置Calendar对象
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, second);
                Log.i("AlarmTest",hourOfDay+":"+minute+":"+second);
                // 设置AlarmManager将在Calendar对应的时间启动指定组件
                long alarmTime = c.getTimeInMillis();
                if (systemTime > alarmTime){
                    c.add(Calendar.DAY_OF_YEAR,1);
                    alarmTime =c.getTimeInMillis();
                }
                long time = alarmTime - systemTime;
                firstTime+= time;
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        firstTime, pendingIntent);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//SDK API >= 23
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//SDK API >= 19 && SDK API < 23
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                } else {//SDK API < 19
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), TIME_INTERVAL, pendingIntent);
                }*/
            } else {
                viewHolder.tv_lunch_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
                viewHolder.tv_clocktime_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
                viewHolder.tv_tagAndjson_clock.setTextColor(activity.getResources().getColor(R.color.colorSimpleDark));
                isChoose.put(position, false);
                listChoose.set(position, false);
                if(alarmManager==null){
                    alarmManager = (AlarmManager)activity.getSystemService(activity.ALARM_SERVICE);
                }
                Intent intent = new Intent(activity,AlarmClockReceiver.class);
                intent.setAction(Global.Action+"."+clock.getId()); // 创建PendingIntent对象
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, 0);
                alarmManager.cancel(pendingIntent);
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
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            Button btn = view.findViewById(R.id.btn_deleteButton_clock);
            RelativeLayout real = view.findViewById(R.id.rl_item_clock);
            TextView tv_row = view.findViewById(R.id.tv_row_clock);
            int ids = btn.getVisibility();

            btn.setVisibility(View.GONE);
            real.setPadding(0, 0, 0, 0);
            tv_row.setPadding(0, 0, 0, 0);
            if (ids == 0) {
                return true;
            }
        }
        return false;
    }

    public static void initDeleteButton() {
        for (int i = 0; i < parent.getChildCount(); i++) {
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
