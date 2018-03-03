package com.example.asd.clock;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asd.clock.Fragment.Adapter.ClockAdapter;
import com.example.asd.clock.Fragment.ClockFragment;
import com.example.asd.clock.Fragment.StopWatchFragment;
import com.example.asd.clock.Fragment.TimerFragment;
import com.example.asd.clock.Fragment.WorldClockFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    //首页底部四个选项(世界时钟，闹钟，秒表，计时器)
    @BindView(R.id.ll_worldclock)
    LinearLayout ll_worldclock;
    @BindView(R.id.ll_clock)
    LinearLayout ll_clock;
    @BindView(R.id.ll_stopwatch)
    LinearLayout ll_stopwatch;
    @BindView(R.id.ll_timer)
    LinearLayout ll_timer;

    //底部四个选项的图像
    @BindView(R.id.iv_worldclock)
    ImageView iv_worldclock;
    @BindView(R.id.iv_clock)
    ImageView iv_clock;
    @BindView(R.id.iv_stopwatch)
    ImageView iv_stopwatch;
    @BindView(R.id.iv_timer)
    ImageView iv_timer;

    //底部四个选项的文字
    @BindView(R.id.tv_worldclock)
    TextView tv_worldclock;
    @BindView(R.id.tv_clock)
    TextView tv_clock;
    @BindView(R.id.tv_stopwatch)
    TextView tv_stopwatch;
    @BindView(R.id.tv_timer)
    TextView tv_timer;

    //四个选项的fragment
    public Fragment mWorldClock;
    public Fragment mClock;
    public Fragment mStopWatch;
    public Fragment mTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //黄油刀 绑定插件操作
        ButterKnife.bind(this);
        //秒表响应跳转到选项四界面
        Intent it = getIntent();
        //接收参数
        long totalTimes = it.getLongExtra("totalTimer",-1);
        //呈现选项界面
        if(totalTimes!=-1){
            selectView(3);
        }else{
            selectView(0);
        }
    }

 /*   @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        ClockAdapter.List2String(ClockAdapter.listChoose);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ClockAdapter.initSwitch();
    }*/
    //当应用退出时候，保存闹钟触发选择集合
    @Override
    protected void onPause() {
        super.onPause();
        ClockAdapter.List2String(ClockAdapter.listChoose,MainActivity.this);
        /*try {
            ClockAdapter.correctIdBool();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }*/
    }

    //监听四个选项点击事件
    @OnClick({R.id.ll_stopwatch, R.id.ll_clock, R.id.ll_timer, R.id.ll_worldclock})
    public void onClick(View v) {
        //初始化数据
        resetImageAndText();
        switch (v.getId()) {
            //选择世界时钟
            case R.id.ll_worldclock:
                selectView(0);
                break;
            //选择闹钟
            case R.id.ll_clock:
                selectView(1);
                break;
            //选择秒表
            case R.id.ll_stopwatch:
                selectView(2);
                break;
            //选择计时器
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
        //选项文字背景颜色-白色
        tv_worldclock.setTextColor(Color.parseColor("#ffffff"));
        tv_clock.setTextColor(Color.parseColor("#ffffff"));
        tv_stopwatch.setTextColor(Color.parseColor("#ffffff"));
        tv_timer.setTextColor(Color.parseColor("#ffffff"));
    }
    //选项显示fragment页面
    private void selectView(int i) {
        //初始化fragment管理者
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragments(transaction);//先隐藏所有页面 再显示点击想显示的页面
        switch (i) {
            case 0://显示世界时钟页面
                //改变底部选项图像和文字颜色
                iv_worldclock.setImageResource(R.drawable.worldclock_press);
                tv_worldclock.setTextColor(getResources().getColor(R.color.colorOrange));
                if (mWorldClock == null) {//fragment为空 添加该页面
                    mWorldClock = new WorldClockFragment(this);
                    transaction.add(R.id.frameLayout, mWorldClock);
                }else{
                    //显示该页面
                    transaction.show(mWorldClock);
                }
                break;
            case 1://显示时钟页面
                //改变底部选项图像和文字颜色
                iv_clock.setImageResource(R.drawable.clock_press);
                tv_clock.setTextColor(getResources().getColor(R.color.colorOrange));
                if (mClock == null) {
                    mClock = new ClockFragment();//fragment为空 添加该页面
                    transaction.add(R.id.frameLayout, mClock);
                }else{
                    //显示该页面
                    transaction.show(mClock);
                }
                break;
            case 2://显示秒表页面
                //改变底部选项图像和文字颜色
                iv_stopwatch.setImageResource(R.drawable.stopwatch_press);
                tv_stopwatch.setTextColor(getResources().getColor(R.color.colorOrange));
                if (mStopWatch == null) {
                    mStopWatch = new StopWatchFragment();//fragment为空 添加该页面
                    transaction.add(R.id.frameLayout, mStopWatch);
                }else{
                    //显示该页面
                    transaction.show(mStopWatch);
                }
                break;
            case 3://显示计时器页面
                //改变底部选项图像和文字颜色
                iv_timer.setImageResource(R.drawable.timer_press);
                tv_timer.setTextColor(getResources().getColor(R.color.colorOrange));
                if (mTimer == null) {
                    mTimer = new TimerFragment();//fragment为空 添加该页面
                    transaction.add(R.id.frameLayout, mTimer);
                }else{
                    //显示该页面
                    transaction.show(mTimer);
                }
                break;
            default:
                break;
        }
        //提交页面
        transaction.commit();
    }
    //隐藏所有选项 根据点击显示
    private void hideFragments(FragmentTransaction transaction) {
        if (mWorldClock != null) {//隐藏该页面
            transaction.hide(mWorldClock);
        }
        if (mClock != null) {//隐藏该页面
            transaction.hide(mClock);
        }
        if (mStopWatch != null) {//隐藏该页面
            transaction.hide(mStopWatch);
        }
        if (mTimer != null) {//隐藏该页面
            transaction.hide(mTimer);
        }
    }

}
