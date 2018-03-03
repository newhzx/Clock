package com.example.asd.clock.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asd.clock.Fragment.Base.BaseFragment;
import com.example.asd.clock.R;
import com.example.asd.clock.Timer.TimerMusic;
import com.example.asd.clock.Timer.TimerReceiver;
import com.example.asd.clock.Utils.Global;
import com.example.asd.clock.Utils.InitClockData;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

//计时器界面
public class TimerFragment extends BaseFragment implements View.OnClickListener {
    private SoundPool soundPool;//声音池
    private List<String> listHour, listMinute, listSecond;//时间滚轮集合
    private LoopView loopViewHoursInTimer, loopViewMinuteInTimer, loopViewSecondInTimer;//时间滚轮
    private Button cancel, pause;//取消和暂停按钮
    private boolean isPause, isStart, isCancel;//暂停 开始和取消标志
    private String music;//响起音乐选择
    private TextView tv_timer_music;//显示该音乐选择
    private boolean isTimer;//是否显示倒计时
    private TextView tv_timer_fragment;//显示倒计时
    private LinearLayout ll_timer_loopview;//滚轮
    private View views;
    private int hourSelect, minuteSelect, secondSelect;//时分秒
    private long totalTime;//总读秒
    private long pauseTime;//当前读秒
    private AlarmManager alarmManager;//闹钟管理类
    private CountDownTimer timer = null;//倒计时对象
    //刷新界面
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            refreshUI();
        }
    };

    //刷新界面方法 根据isTimer决定显示滚轮还是倒计时界面
    private void refreshUI() {
        if (!isTimer) {
            ll_timer_loopview.setVisibility(View.VISIBLE);
            tv_timer_fragment.setVisibility(View.GONE);
        } else {
            ll_timer_loopview.setVisibility(View.GONE);
            tv_timer_fragment.setVisibility(View.VISIBLE);
        }
    }
    //刷新界面 显示倒计时
    private void refreshTimerUI(Long totalTime) {
        Log.i("pauseTime",pauseTime+"");
        String remainMinute = null,remainSecond = null;
        //总读秒 拆分成时分秒格式 并显示出来
        Long hour = totalTime/3600;
        Long minute = totalTime % 3600 / 60;
        Long second = totalTime % 60;
        if(minute<10){
            remainMinute = "0"+ minute;
        }else{
            remainMinute = minute +"";
        }
        if(second<10){
            remainSecond = "0" + second;
        }else{
            remainSecond = second +"";
        }
        if (hour == 0){
            tv_timer_fragment.setText(remainMinute+":"+remainSecond);
        }else{
            tv_timer_fragment.setText(hour+":"+remainMinute+":"+remainSecond);
        }
    }

    @Override
    public View baseView(View view) {
        title.setText("计时器");
        edit.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        //获取倒计时结束活动传过来的totalTimes参数
        Intent it = activity.getIntent();
        long totalTimes = it.getLongExtra("totalTimer",-1);
        //加载界面
        views = View.inflate(activity, R.layout.timer, null);
        //声音池——初始化 加载 播放
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundPool.load(activity, R.raw.collide, 1);
        soundPool.play(1, 1, 1, 0, 0, 2);
        initViews(views);//初始化控件
        initLoopView(views);//初始化滚轮
        initListener();//滚轮添加侦听事件
        //当totalTimes 有数值的时候  初始化倒计时
        if(totalTimes!=-1){
            Log.i("totalTimesk","totalTimesk:"+totalTimes);
            isTimer = true;
            isStart = true;
            isPause = false;
            isCancel = true;
            cancel.setSelected(true);//设置可选状态
            cancel.setClickable(true);//设置可点击状态
            pause.setSelected(true);//暂停状态
            pause.setText("暂停");
            pause.setTextColor(Color.parseColor("#f39800"));
            totalTime = totalTimes;
            initData(totalTime);//初始化倒计时界面
            //刷新界面
            mHandler.sendMessage(mHandler.obtainMessage());
        }
        basefragment.addView(views);
        return view;
    }
    //滚轮添加侦听事件
    private void initListener() {
        loopViewHoursInTimer.setListener(new OnItemSelected());
        loopViewMinuteInTimer.setListener(new OnItemSelected());
        loopViewSecondInTimer.setListener(new OnItemSelected());
    }
    //音乐设置回调
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 84) {
            music = data.getStringExtra("music");
            tv_timer_music.setText(music);
        }
    }
    //界面初始化
    private void initViews(View view) {
        tv_timer_music = (TextView) view.findViewById(R.id.tv_timer_music);
        tv_timer_fragment = (TextView) view.findViewById(R.id.tv_timer_fragment);

        view.findViewById(R.id.rl_timer_music).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, TimerMusic.class), 1);
            }
        });
        //取消和暂停按钮实例化并添加侦听事件
        cancel = (Button) view.findViewById(R.id.timer_cancel);
        pause = (Button) view.findViewById(R.id.timer_pause);
        pause.setOnClickListener(this);
        cancel.setOnClickListener(this);
        cancel.setClickable(false);
    }

    private void initColorAndLine() {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timer_cancel:
                if(isCancel){
                    isStart = false;//开始按钮
                    isPause = false;//切换暂停状态
                    cancel.setSelected(false);//不可选择事件
                    cancel.setClickable(false);//不可点击事件
                    pause.setSelected(false);//设置开始底色
                    pause.setText("开始计时");//设置开始字样和颜色
                    pause.setTextColor(Color.parseColor("#ffffff"));//
                    cancelTimer();//取消倒计时
                }
                isTimer = false;
                break;
            case R.id.timer_pause:
                if(!isStart){
                    initData();//初始化并启动倒计时
                    isStart = true;//只有暂停和继续
                    isCancel = true;//设置取消按钮
                    cancel.setSelected(true);//设置可选状态
                    cancel.setClickable(true);//设置可点击状态
                    pause.setSelected(true);//显示暂停状态
                    pause.setText("暂停");//暂停字样和颜色
                    pause.setTextColor(Color.parseColor("#f39800"));
                }else{
                    if (isPause) {
                        isPause = false;
                        pause.setSelected(true);
                        pause.setText("暂停");
                        pause.setTextColor(Color.parseColor("#f39800"));
                        if (pauseTime != 0) {
                            //将上次当前剩余时间作为新的时长
                            initCountDownTimer(pauseTime+50, Global.countDownInterval);
                            timer.start();
                        }
                    } else {
                        //设置继续按钮
                        isPause = true;
                        pause.setSelected(false);
                        pause.setText("继续");
                        pause.setTextColor(Color.parseColor("#ffffff"));
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                    }
                }
                isTimer = true;
                break;
        }
        mHandler.sendMessage(mHandler.obtainMessage());
    }
    //获取滚轮数值并初始化倒计时 还有开始
    private void initData() {
        hourSelect = loopViewHoursInTimer.getSelectedItem();
        minuteSelect = loopViewMinuteInTimer.getSelectedItem();
        secondSelect = loopViewSecondInTimer.getSelectedItem();
        totalTime = hourSelect * 60 * 60 + minuteSelect * 60 + secondSelect;
        initCountDownTimer(totalTime*1000+1050, Global.countDownInterval);
        timer.start();
    }
    //初始化倒计时并开始
    private void initData(long totalTimes) {
        initCountDownTimer(totalTimes*1000+1500, Global.countDownInterval);
        timer.start();
    }
    //实例化倒计时对象
    public void initCountDownTimer(long millisInFuture,int countDownInteval) {
        timer = new CountDownTimer(millisInFuture,countDownInteval) {
            @Override
            public void onTick(long millisUntilFinished) {
                pauseTime = millisUntilFinished;//当前剩余时间;
                refreshTimerUI(millisUntilFinished/1000-1);//刷新界面
                Log.i("TAGSSS", "seconds remaining: " + millisUntilFinished/1000);
            }
            //结束时调用闹钟活动界面
            public void onFinish() {
                TimerClock();
                Log.i("TAGSSS", "done!");
            }
        };
    }

    private void TimerClock() {
        if(alarmManager==null){//实例化闹钟管理类对象
            alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        }
        Intent intent = new Intent(activity, TimerReceiver.class);
        intent.putExtra("totalTimer",totalTime);
        intent.setAction(Global.Action+".timer");//设置action 区别别的广播
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //发送广播
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                0, pendingIntent);
    }
    //添加滚轮滚动声音
    class OnItemSelected implements OnItemSelectedListener {
        public void onItemSelected(int index) {
            soundPool.load(activity, R.raw.collide, 1);
            soundPool.play(1, 1, 1, 0, 0, 2);
        }
    }
    //初始化滚轮
    private void initLoopView(View view) {
        ll_timer_loopview = (LinearLayout) view.findViewById(R.id.ll_timer_loopview);
        //初始化滚轮
        loopViewHoursInTimer = (LoopView) view.findViewById(R.id.loopViewHourInTimer);
        loopViewMinuteInTimer = (LoopView) view.findViewById(R.id.loopViewMinuteInTimer);
        loopViewSecondInTimer = (LoopView) view.findViewById(R.id.loopViewSecondInTimer);

        initColorAndLine();//初始化颜色

        //滚轮时—分—秒集合初始化
        listHour = new ArrayList<String>();
        listMinute = new ArrayList<String>();
        listSecond = new ArrayList<String>();
        //滚轮填充数据
        listHour = InitClockData.getLoopViewHourInTimer(listHour);
        listMinute = InitClockData.getLoopViewMinuteInTimer(listMinute);
        listSecond = InitClockData.getLoopViewSecondInTimer(listSecond);
        //设置原始数据
        loopViewHoursInTimer.setItems(listHour);
        loopViewHoursInTimer.setNotLoop();
        loopViewMinuteInTimer.setItems(listMinute);
        loopViewMinuteInTimer.setNotLoop();
        loopViewSecondInTimer.setItems(listSecond);
        loopViewSecondInTimer.setNotLoop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }
    //置空倒计时对象
    private void cancelTimer(){
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
