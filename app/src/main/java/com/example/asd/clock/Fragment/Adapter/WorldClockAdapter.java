package com.example.asd.clock.Fragment.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asd.clock.Fragment.Bean.WorldClock;
import com.example.asd.clock.R;
import com.example.asd.clock.Utils.Utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

//世界时钟适配器
public class WorldClockAdapter extends BaseAdapter{
    private int right;//离右侧的距离
    private Activity activity;//
    private RelativeLayout.LayoutParams lp;
    public static ViewGroup parent;
    private static List<WorldClock> list;//世界时钟list集合
    private boolean isEdit = false;//是否编辑状态
    private static HashMap<Integer,Boolean> isCitySelect = new HashMap<Integer,Boolean>();
//    初始化界面
    public WorldClockAdapter(Activity activity, List<WorldClock> list) {
        this.activity = activity;
        this.list = list;
        initData();
    }
    public static void initData() {
        isCitySelect.clear();
        for (int i = 0; i < list.size(); i++) {
            isCitySelect.put(i, false);
        }
    }
    //刷新listview
    public void updateListView(boolean isEdit) {
        this.isEdit = isEdit;
        Log.i("AdapterisEdit", String.valueOf(isEdit));
        notifyDataSetChanged();
    }

    public static HashMap<Integer,Boolean> getCitySelect() {
        return isCitySelect;
    }

    public static void setCitySelect(HashMap<Integer, Boolean> isCitySelect) {
        WorldClockAdapter.isCitySelect = isCitySelect;
    }

    //刷新listview
    public void updateListView(List<WorldClock> list, boolean isEdit) {
        this.list = list;
        this.isEdit = isEdit;
        Log.i("AdapterisEdit", String.valueOf(isEdit));
        WorldClockAdapter.initData();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public WorldClock getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        this.parent = parent;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
//            该item的实例化  不是编辑状态下的四个文本框
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_worldclock, null);
            viewHolder.tv_hours = (TextView) convertView.findViewById(R.id.tv_hours);
            viewHolder.tv_city_cn = (TextView) convertView.findViewById(R.id.tv_city_cn);
            viewHolder.tv_lunch_worldclock = (TextView) convertView.findViewById(R.id.tv_lunch_worldclock);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

            //编辑状态需要用到的控件内容  实例化并存在viewholder中
            viewHolder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
            viewHolder.ll_threeview = (LinearLayout) convertView.findViewById(R.id.ll_threeview);
            viewHolder.rl_delete_botton_worldclock = (RelativeLayout)convertView.findViewById(R.id.rl_delete_botton_worldclock);
            viewHolder.rl_item = (RelativeLayout) convertView.findViewById(R.id.rl_item);
            viewHolder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_content);
            viewHolder.btn_delete = (Button) convertView.findViewById(R.id.btn_delete);
            viewHolder.iv_delete_item_worldclock = (ImageView) convertView.findViewById(R.id.iv_delete_item_worldclock);
//            把viewholder存入到convertview中
//            viewHolder.btn_delete.setTag(position);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        //编辑状态中
        if (isEdit) {
            //编辑状态需要显示的控件
            viewHolder.ll_item.setPadding(0, 0, 0, 0);
            viewHolder.ll_threeview.setVisibility(View.VISIBLE);
            viewHolder.rl_delete_botton_worldclock.setVisibility(View.VISIBLE);
            lp.setMargins(120, 0, 10, 0);
            viewHolder.rl_content.setLayoutParams(lp);
            viewHolder.iv_delete_item_worldclock.setMinimumHeight(viewHolder.rl_content.getHeight());

            viewHolder.rl_delete_botton_worldclock.setMinimumHeight(viewHolder.rl_item.getHeight());

            //编辑状态需要隐藏的按钮
            viewHolder.tv_lunch_worldclock.setVisibility(View.GONE);
            viewHolder.tv_time.setVisibility(View.GONE);
        } else {
            //非编辑状态 隐藏控件内容
            viewHolder.ll_item.setPadding(0, 0, 0, 0);
            lp.setMargins(40, 0, 10, 0);
            viewHolder.rl_content.setLayoutParams(lp);

            viewHolder.rl_delete_botton_worldclock.setVisibility(View.GONE);
            viewHolder.ll_threeview.setVisibility(View.GONE);
            viewHolder.btn_delete.setVisibility(View.GONE);
            viewHolder.rl_content.setPadding(0, 0, 0, 0);
            // 非编辑状态 显示文本控件
            viewHolder.tv_lunch_worldclock.setVisibility(View.VISIBLE);
            viewHolder.tv_time.setVisibility(View.VISIBLE);
        }
        //获取当前选中的item对象
        WorldClock wc = getItem(position);
        viewHolder.tv_city_cn.setText(wc.getCity_cn());//城市中文名
        //系统时间
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);//时
        int minute = c.get(Calendar.MINUTE);//分
        int second = c.get(Calendar.SECOND);//秒
        Log.i("text#", hour + "#" + minute + "#" + second);
        int disTime = Integer.parseInt(wc.getHours());//与系统相差时间
        int currentHour = disTime + hour;//当前时间
        //相差时间和当前状态
        if (currentHour >= 0 && currentHour < 24) {
            viewHolder.tv_hours.setText("今天, " + wc.getHours() + "小时");
        } else if (currentHour >= 24) {
            viewHolder.tv_hours.setText("明天, " + wc.getHours() + "小时");
        } else {
            viewHolder.tv_hours.setText("昨天, " + wc.getHours() + "小时");
        }
        //上下午
        if (currentHour >= 0 && currentHour < 12 || currentHour >= 24 || currentHour < -12) {
            viewHolder.tv_lunch_worldclock.setText("上午");
        } else {
            viewHolder.tv_lunch_worldclock.setText("下午");
        }
        //显示时间
        while (currentHour > 12) currentHour = currentHour - 12;
        while (currentHour <= 0) currentHour = currentHour + 12;
        if (minute < 10) {
            viewHolder.tv_time.setText(currentHour + ":0" + minute);
        } else {
            viewHolder.tv_time.setText(currentHour + ":" + minute);
        }

        final View finalConvertView = convertView;
        viewHolder.rl_delete_botton_worldclock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClicksListener.onClicks(finalConvertView, position, list);
            }
        });

        if (getCitySelect().get(position)) {
            viewHolder.rl_item.setPadding(-120, viewHolder.rl_content.getPaddingTop(), right, viewHolder.rl_content.getPaddingBottom());

            viewHolder.btn_delete.setPadding(0, 0, 0, 0);
            viewHolder.btn_delete.setVisibility(View.VISIBLE);
            viewHolder.btn_delete.setHeight(viewHolder.rl_content.getHeight());
        } else {
            viewHolder.rl_item.setPadding(0, viewHolder.rl_content.getPaddingTop(), right, viewHolder.rl_content.getPaddingBottom());
            viewHolder.rl_content.setPadding(0, 0, 0, 0);
            //退出删除按钮
            viewHolder.btn_delete.setPadding(0, 0, 0, 0);
            viewHolder.btn_delete.setVisibility(View.GONE);
        }

        return convertView;
    }
    //交换位置方法
    public void change(int start, int end) {
        WorldClock srcData = list.get(start);
        list.remove(srcData);
        list.add(end, srcData);
        if (start == end) return;
        Utils.setList(list);
    }

    public interface onItemClicksListener {
        void onClicks(View view, int position, List<WorldClock> list);
    }
    private onItemClicksListener mOnItemClicksListener;

    public void setOnItemClicksListener(onItemClicksListener mOnItemClicksListener) {
        this.mOnItemClicksListener = mOnItemClicksListener;
    }

    public static void initDeleteButton() {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            Button btn_delete = view.findViewById(R.id.btn_delete);
            RelativeLayout rl_item = view.findViewById(R.id.rl_item);

            btn_delete.setVisibility(View.GONE);
            rl_item.setPadding(0, 0, 0, 0);
        }
    }
   /* //注册事件所实例化的class类 实现按钮的效果
    class ListViewItemListener implements View.OnClickListener {
        int position;
        ViewHolder viewHolder;
        int right;
        public ListViewItemListener(){}
        //注册事件构造方法
        public ListViewItemListener(int position, ViewHolder viewHolder) {
            this.position = position;
            this.viewHolder = viewHolder;
        }
        //重载
        public ListViewItemListener(int position, ViewHolder viewHolder, int right) {
            this.position = position;
            this.viewHolder = viewHolder;
            this.right = right;
        }
//        要实现的click方法
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //点击item的效果
                case R.id.rl_content:
                    viewHolder.rl_content.setPadding(40, viewHolder.rl_content.getPaddingTop(), 0, viewHolder.rl_content.getPaddingBottom());

                    viewHolder.btn_delete.setVisibility(View.GONE);
                    break;
                    //点击删除按钮的操作
               case R.id.btn_delete:
                   if(Utils.getLists().size()>0){
                       String city_en = list.get(position).getCity_en();
                       list.remove(position);
                       Utils.setLists(list);
                       viewHolder.rl_content.setPadding(40, viewHolder.rl_content.getPaddingTop(), 0, viewHolder.rl_content.getPaddingBottom());
                       viewHolder.btn_delete.setVisibility(View.GONE);

                       notifyDataSetChanged();
                       Utils.showToast(activity, city_en);
                       removeItem(city_en);
                       Log.i("notifyDataSetChanged()",city_en+""+position);
                   }
                   break;
                //弹出删除按钮的按钮
                case R.id.rl_delete_botton_worldclock:
                    Utils.showToast(activity, list.get(position).getCity_cn());
                    viewHolder.rl_content.setPadding(-100, viewHolder.rl_content.getPaddingTop(), right, viewHolder.rl_content.getPaddingBottom());

                    viewHolder.btn_delete.setPadding(0, 0, 0, 0);
                    viewHolder.btn_delete.setVisibility(View.VISIBLE);
                    viewHolder.btn_delete.setHeight(viewHolder.rl_content.getHeight());
                    viewHolder.btn_delete.setOnClickListener(new ListViewItemListener(position, viewHolder));
                    break;
                case R.id.ll_threeview:
                    Toast.makeText(activity,"点击事件",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }*/
//   内部类
    class ViewHolder {
        TextView tv_city_cn;
        TextView tv_hours;
        TextView tv_lunch_worldclock;
        TextView tv_time;
        RelativeLayout rl_delete_botton_worldclock;
        LinearLayout ll_item;
        LinearLayout ll_threeview;
        RelativeLayout rl_content;
        Button btn_delete;
        RelativeLayout rl_item;
        ImageView iv_delete_item_worldclock;
    }
}
