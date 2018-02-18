package com.example.asd.clock.Fragment;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;

import com.example.asd.clock.Fragment.Base.BaseFragment;
import com.example.asd.clock.R;
import com.example.asd.clock.Utils.InitClockData;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

//计时器界面
public class TimerFragment extends BaseFragment {
    private SoundPool soundPool;//声音池
    private List<String> list, listHour, listMinute, listSecond;//时间滚轮集合
    private LoopView loopViewline, loopViewlines, loopViewHoursInTimer, loopViewMinuteInTimer, loopViewSecondInTimer;//时间滚轮
    @Override
    public View baseView(View view) {
        title.setText("计时器");
        edit.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        View views = View.inflate(activity, R.layout.timer,null);
        //声音池——初始化 加载 播放
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundPool.load(activity, R.raw.collide, 1);
        soundPool.play(1, 1, 1, 0, 0, 2);
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          initViews(views);//初始化控件
        initLoopView();
        initListener();
        basefragment.addView(views);
        return view;
    }

    private void initListener() {
        loopViewHoursInTimer.setListener(new OnItemSelected());
        loopViewMinuteInTimer.setListener(new OnItemSelected());
        loopViewSecondInTimer.setListener(new OnItemSelected());
    }

    private void initViews(View view) {
        //初始化滚轮
       /* loopViewline = (LoopView) view.findViewById(R.id.loopViewlineInTimer);
        loopViewlines = (LoopView) view.findViewById(R.id.loopViewlinesInTimer);*/
        loopViewHoursInTimer = (LoopView) view.findViewById(R.id.loopViewHourInTimer);
        loopViewMinuteInTimer = (LoopView) view.findViewById(R.id.loopViewMinuteInTimer);
        loopViewSecondInTimer = (LoopView) view.findViewById(R.id.loopViewSecondInTimer);
        initColorAndLine();//初始化颜色
    }

    private void initColorAndLine() {
        //添加两侧滚轮分割线颜色
   /*     loopViewline.setDividerColor(Color.parseColor("#bb666666"));
        loopViewlines.setDividerColor(Color.parseColor("#bb666666"));*/
        //添加上下午滚轮分割线 字体大小 颜色
        loopViewHoursInTimer.setDividerColor(Color.parseColor("#bb666666"));
        loopViewHoursInTimer.setOuterTextColor(Color.parseColor("#bb666666"));
        loopViewHoursInTimer.setCenterTextColor(Color.parseColor("#ffffff"));
        loopViewHoursInTimer.setTextSize(18);
        //添加时滚轮分割线 字体大小 颜色
        loopViewMinuteInTimer.setDividerColor(Color.parseColor("#bb666666"));
        loopViewMinuteInTimer.setOuterTextColor(Color.parseColor("#bb666666"));
        loopViewMinuteInTimer.setCenterTextColor(Color.parseColor("#ffffff"));
        loopViewMinuteInTimer.setTextSize(18);
        //添加分滚轮分割线 字体大小 颜色
        loopViewSecondInTimer.setDividerColor(Color.parseColor("#bb666666"));
        loopViewSecondInTimer.setOuterTextColor(Color.parseColor("#bb666666"));
        loopViewSecondInTimer.setCenterTextColor(Color.parseColor("#ffffff"));
        loopViewSecondInTimer.setTextSize(18);
    }

    class OnItemSelected implements OnItemSelectedListener {
        public void onItemSelected(int index) {
            soundPool.load(activity, R.raw.collide, 1);
            soundPool.play(1, 1, 1, 0, 0, 2);
        }
    }
    private  void initLoopView(){
        //滚轮时—分—秒集合初始化
     /*   list = new ArrayList<String>();*/
        listHour = new ArrayList<String>();
        listMinute = new ArrayList<String>();
        listSecond = new ArrayList<String>();
        //滚轮填充数据
//        list.add("");
        listHour = InitClockData.getLoopViewHourInTimer(listHour);
        listMinute = InitClockData.getLoopViewMinuteInTimer(listMinute);
        listSecond = InitClockData.getLoopViewSecondInTimer(listSecond);
        //设置原始数据
//        loopViewline.setItems(list);
        loopViewHoursInTimer.setItems(listHour);
        loopViewHoursInTimer.setNotLoop();
        loopViewMinuteInTimer.setItems(listMinute);
        loopViewMinuteInTimer.setNotLoop();
        loopViewSecondInTimer.setItems(listSecond);
        loopViewSecondInTimer.setNotLoop();
//        loopViewlines.setItems(list);
    }
}
