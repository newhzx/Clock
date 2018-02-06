package com.example.asd.clock;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asd.clock.Fragment.ClockFragment;
import com.example.asd.clock.Fragment.StopWatchFragment;
import com.example.asd.clock.Fragment.TimerFragment;
import com.example.asd.clock.Fragment.WorldClockFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    //控件
    @BindView(R.id.ll_worldclock)
    LinearLayout ll_worldclock;
    @BindView(R.id.ll_clock)
    LinearLayout ll_clock;
    @BindView(R.id.ll_stopwatch)
    LinearLayout ll_stopwatch;
    @BindView(R.id.ll_timer)
    LinearLayout ll_timer;

    @BindView(R.id.iv_worldclock)
    ImageView iv_worldclock;
    @BindView(R.id.iv_clock)
    ImageView iv_clock;
    @BindView(R.id.iv_stopwatch)
    ImageView iv_stopwatch;
    @BindView(R.id.iv_timer)
    ImageView iv_timer;

    @BindView(R.id.tv_worldclock)
    TextView tv_worldclock;
    @BindView(R.id.tv_clock)
    TextView tv_clock;
    @BindView(R.id.tv_stopwatch)
    TextView tv_stopwatch;
    @BindView(R.id.tv_timer)
    TextView tv_timer;

    public Fragment mWorldClock;
    public Fragment mClock;
    public Fragment mStopWatch;
    public Fragment mTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        selectView(0);
    }
    //监听事件
    @OnClick({R.id.ll_stopwatch, R.id.ll_clock, R.id.ll_timer, R.id.ll_worldclock})
    public void onClick(View v) {
        //初始化数据
        resetImageAndText();
        switch (v.getId()) {
            case R.id.ll_worldclock:
                selectView(0);
                break;
            case R.id.ll_clock:
                selectView(1);
                break;
            case R.id.ll_stopwatch:
                selectView(2);
                break;
            case R.id.ll_timer:
                selectView(3);
                break;
            default:
                break;
        }
    }
    //初始化界面
    private void resetImageAndText() {
        //选项一 世界时钟
        iv_worldclock.setImageResource(R.drawable.worldclock_default);
        //选项二 闹钟
        iv_clock.setImageResource(R.drawable.clock_default);
        //选项三 秒表
        iv_stopwatch.setImageResource(R.drawable.stopwatch_default);
        //选项四 计时器
        iv_timer.setImageResource(R.drawable.timer_default);

        //选项背景颜色
        tv_worldclock.setTextColor(getResources().getColor(R.color.colorDark));
        tv_clock.setTextColor(getResources().getColor(R.color.colorDark));
        tv_stopwatch.setTextColor(getResources().getColor(R.color.colorDark));
        tv_timer.setTextColor(getResources().getColor(R.color.colorDark));
    }
    //选项跳转页面
    private void selectView(int i) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragments(transaction);
        switch (i) {
            case 0:
                iv_worldclock.setImageResource(R.drawable.worldclock_press);
                tv_worldclock.setTextColor(getResources().getColor(R.color.colorOrange));
                if (mWorldClock == null) {
                    mWorldClock = new WorldClockFragment(this);
                    transaction.add(R.id.frameLayout, mWorldClock);
                }else{
                    transaction.show(mWorldClock);
                }
                break;
            case 1:
                iv_clock.setImageResource(R.drawable.clock_press);
                tv_clock.setTextColor(getResources().getColor(R.color.colorOrange));
                if (mClock == null) {
                    mClock = new ClockFragment();
                    transaction.add(R.id.frameLayout, mClock);
                }else{
                    transaction.show(mClock);
                }
                break;
            case 2:
                iv_stopwatch.setImageResource(R.drawable.stopwatch_press);
                tv_stopwatch.setTextColor(getResources().getColor(R.color.colorOrange));
                if (mStopWatch == null) {
                    mStopWatch = new StopWatchFragment();
                    transaction.add(R.id.frameLayout, mStopWatch);
                }else{
                    transaction.show(mStopWatch);
                }
                break;
            case 3:
                iv_timer.setImageResource(R.drawable.timer_press);
                tv_timer.setTextColor(getResources().getColor(R.color.colorOrange));
                if (mTimer == null) {
                    mTimer = new TimerFragment();
                    transaction.add(R.id.frameLayout, mTimer);
                }else{
                    transaction.show(mTimer);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }
    //隐藏所有选项 根据点击显示
    private void hideFragments(FragmentTransaction transaction) {
        if (mWorldClock != null) {
            transaction.hide(mWorldClock);
        }
        if (mClock != null) {
            transaction.hide(mClock);
        }
        if (mStopWatch != null) {
            transaction.hide(mStopWatch);
        }
        if (mTimer != null) {
            transaction.hide(mTimer);
        }
    }

}
