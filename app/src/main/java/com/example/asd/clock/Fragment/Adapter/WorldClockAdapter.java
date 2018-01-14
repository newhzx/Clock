package com.example.asd.clock.Fragment.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asd.clock.Fragment.Bean.WorldClock;
import com.example.asd.clock.R;
import com.example.asd.clock.Utils.Utils;
import com.example.asd.clock.Utils.XMLUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by asd on 2018/1/6.
 */
//世界时钟适配器
public class WorldClockAdapter extends BaseAdapter{
    private int right;//离右侧的距离
    private Activity activity;//
    private List<WorldClock> list;//世界时钟list集合
    private boolean isEdit = false;//是否编辑状态
//    初始化界面
    public WorldClockAdapter(Activity activity, List<WorldClock> list) {
        this.activity = activity;
        this.list = list;
    }
    //刷新listview
    public void updateListView(List<WorldClock> list, boolean isEdit) {
        this.list = list;
        this.isEdit = isEdit;
        Log.i("AdapterisEdit", String.valueOf(isEdit));
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
//            该item的实例化  不是编辑状态下的四个文本框
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_worldclock, null);
            viewHolder.tv_hours = (TextView) convertView.findViewById(R.id.tv_hours);
            viewHolder.tv_city_cn = (TextView) convertView.findViewById(R.id.tv_city_cn);
            viewHolder.tv_lunch = (TextView) convertView.findViewById(R.id.tv_lunch);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

            //编辑状态需要用到的控件内容  实例化并存在viewholder中
            viewHolder.ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
            viewHolder.ll_threeview = (LinearLayout) convertView.findViewById(R.id.ll_threeview);
            viewHolder.btn_deleteWorldClock = (ImageButton) convertView.findViewById(R.id.delete_worldclock);
            viewHolder.rl_item = (RelativeLayout) convertView.findViewById(R.id.rl_item);
            viewHolder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_content);
            viewHolder.btn_delete = (Button) convertView.findViewById(R.id.btn_delete);
            viewHolder.btn_delete.setTag(position);
//            把viewholder存入到convertview中
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //编辑状态中
        if (isEdit) {
            //编辑状态需要显示的控件
            viewHolder.ll_item.setPadding(60, 0, 0, 0);
            viewHolder.btn_deleteWorldClock.setVisibility(View.VISIBLE);
            viewHolder.ll_threeview.setVisibility(View.VISIBLE);
            //为该item添加侦听事件
            right = viewHolder.btn_delete.getWidth() + viewHolder.rl_content.getPaddingRight();
            viewHolder.btn_deleteWorldClock.setOnClickListener(new ListViewItemListener(position, viewHolder, right));
            viewHolder.rl_content.setOnClickListener(new ListViewItemListener(position, viewHolder, right));
            viewHolder.ll_threeview.setOnClickListener(new ListViewItemListener(position, viewHolder));

            //编辑状态需要隐藏的按钮
            viewHolder.tv_lunch.setVisibility(View.GONE);
            viewHolder.tv_time.setVisibility(View.GONE);
        } else {
            //非编辑状态 隐藏控件内容
            viewHolder.ll_item.setPadding(0, 0, 0, 0);
            viewHolder.btn_deleteWorldClock.setVisibility(View.GONE);
            viewHolder.ll_threeview.setVisibility(View.GONE);
            viewHolder.btn_delete.setVisibility(View.GONE);
            viewHolder.rl_content.setPadding(40, 0, 0, 0);
            // 非编辑状态 显示文本控件
            viewHolder.tv_lunch.setVisibility(View.VISIBLE);
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
            viewHolder.tv_lunch.setText("上午");
        } else {
            viewHolder.tv_lunch.setText("下午");
        }
        //显示时间
        while (currentHour > 12) currentHour = currentHour - 12;
        while (currentHour <= 0) currentHour = currentHour + 12;
        if (minute < 10) {
            viewHolder.tv_time.setText(currentHour + ":0" + minute);
        } else {
            viewHolder.tv_time.setText(currentHour + ":" + minute);
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
    //删除某个节点
    private void removeItem(String city_en) {
        XMLUtils.removeNoteToXML(city_en);
    }

    //注册事件所实例化的class类 实现按钮的效果
    class ListViewItemListener implements View.OnClickListener {
        int position;
        ViewHolder viewHolder;
        int right;
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
                case R.id.delete_worldclock:
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
    }
//   内部类
    class ViewHolder {
        TextView tv_city_cn;
        TextView tv_hours;
        TextView tv_lunch;
        TextView tv_time;
        ImageButton btn_deleteWorldClock;
        LinearLayout ll_item;
        LinearLayout ll_threeview;
        RelativeLayout rl_content;
        Button btn_delete;
        RelativeLayout rl_item;
    }
}
