package com.example.asd.clock.Timer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.asd.clock.MainActivity;
import com.example.asd.clock.R;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.Calendar;

public class TimerActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "TimerRingActivity";
    private TextView tv_time;//当前系统时间
    private TextView tv_tag;//备注信息
    private Button btn_pause;//暂停按钮
    private Button btn_close;//关闭按钮
    private ITimerServiceProvider ServiceProvider;     //服务的中间人接口对象
    private MyConn conn;
    private Intent serviceIntent,it;//跳转到服务意图
    private boolean flag_is_pause_or_close;//区分暂停和关闭的标志
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if(!flag_is_pause_or_close){
                        ServiceProvider.pauseTimerRing();//调用九分钟响应方法
                        TimerActivity.this.finish();//关闭该页面
                        Log.i(TAG, "--------响铃一分钟无反应。。--成功调用服务里九分钟后再响的方法!----------------");
                    }else{
                        Log.i(TAG, "--------点击了暂停或关闭闹钟。--一分钟失效！---------------");
                    }
                }
            }, 60*1000);
        };
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.timer_activity);
        //锁屏状态下 唤醒屏幕 保持常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        //闹钟响起界面
        tv_time = (TextView) this.findViewById(R.id.tv_timer_time);//当前系统时间
        tv_tag = (TextView) this.findViewById(R.id.tv_timer_tag);//备注
        btn_pause = (Button) this.findViewById(R.id.btn_timer_pause);//暂停按钮
        btn_close = (Button) this.findViewById(R.id.btn_timer_close);//关闭按钮
        btn_pause.setOnClickListener(this);//添加点击侦听事件
        btn_close.setOnClickListener(this);
        handler.sendEmptyMessage(0);//刷新界面
    }

    @Override
    protected void onStart() {
        super.onStart();
        //绑定闹钟到来的服务
        conn = new MyConn();
        serviceIntent = new Intent(this, TimerService.class);
        bindService(serviceIntent, conn, 0);
        //当前系统类
        Calendar calendar = Calendar.getInstance();
        String hour = calendar.get(Calendar.HOUR_OF_DAY)+"";
        String minute = calendar.get(Calendar.MINUTE)+"";
        if(hour.length() == 1){
            hour = "0"+hour;
        }
        if(minute.length() == 1){
            minute = "0"+minute;
        }
        tv_time.setText(hour+":"+minute);
        tv_tag.setText("计时结束");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //暂停按钮
            case R.id.btn_timer_pause:
                flag_is_pause_or_close =true;
                ServiceProvider.pauseTimerRing();//调用九分钟后响铃
                //获取receiver传过来的意图
                long totalTimer  = getIntent().getLongExtra("totalTimer",-1);
                Log.i("totalTimes", totalTimer + "");
                //跳转到主页
                it =  new Intent(this, MainActivity.class);
                it.putExtra("totalTimer", totalTimer);
                startActivity(it);
                finish();
                break;
            //关闭按钮
            case R.id.btn_timer_close:
                flag_is_pause_or_close =true;
                try {
                    //关闭界面
                    ServiceProvider.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                finish();
                break;
        }
    }

    private class MyConn implements ServiceConnection {
        //当服务失去连接的时候调用（一般进程挂了，服务被异常杀死）
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
        //当服务被连接的时候调用 服务识别成功 绑定的时候调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServiceProvider = (ITimerServiceProvider) service;
            Log.i(TAG, "-------在activity里面得到了服务的中间人对象-------------");
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        stopService(serviceIntent);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
