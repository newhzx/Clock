package com.example.asd.clock.Timer;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asd.clock.R;
import com.example.asd.clock.Utils.Global;
import com.example.asd.clock.Utils.SoundUtils;
import com.example.asd.clock.Utils.VibratorUtils;

import java.util.List;

public class TimerMusic extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private Button btn_set_timermusic,btn_cancel_timermusic;//添加铃声按钮
    private ListView lv_timermusic;//铃声listview
    private TimerMusicAdapter adapter;//铃声adapter
    private RelativeLayout rl_timermusic_none;//无点击选项
    private CheckBox checkBox;//无的选项
    private TextView tv_music_vibrator;//振动的名称
    private long[] pattern;//振动的频率
    private Vibrator vibrator;//振动的类
    private RingtoneManager ringtoneManager;//铃声管理类
    private Uri ringtone;//铃声的Uri
    private boolean isPlaying = false;//是否在播放铃声
    private static List<String> listRong;//铃声名称集合
    private static List<Uri> listRingtones;//铃声Uri集合
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timermusic);
        initView();//初始化控件
        initData();//初始化数据
        initListener();//初始化事件
    }
    //计算listview的高度 让ScrollView滑动起来
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();//获取adapter
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;//总高度
        //
        for (int i = 0; i < listAdapter.getCount(); i++) {
            //获取item高度
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            //item总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        //获取分割线和item的高度，算出总高度
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        params.height += 5;//上面的分割线设置为5dp,所以这里每次加5个像素
        listView.setLayoutParams(params);//设置params
    }

    private void initData() {
        //初始化数据
        SoundUtils.initMusic(TimerMusic.this);
        listRong = SoundUtils.getListRong();//获取铃声名称集合
        listRingtones = SoundUtils.getListRingtones();//获取铃声Uri集合
        adapter = new TimerMusicAdapter(TimerMusic.this, listRong);
        lv_timermusic.setAdapter(adapter);
        lv_timermusic.setFocusable(false);//取消listview焦点  让swrollview获取焦点
        setListViewHeightBasedOnChildren(lv_timermusic);//设置listview高度
    }

    private void initListener() {
        //添加点击事件
        btn_cancel_timermusic.setOnClickListener(this);
        btn_set_timermusic.setOnClickListener(this);
        lv_timermusic.setOnItemClickListener(this);
        rl_timermusic_none.setOnClickListener(this);
    }
    //初始化布局控件
    private void initView() {
        btn_set_timermusic = (Button) findViewById(R.id.btn_set_timermusic);
        btn_cancel_timermusic = (Button) findViewById(R.id.btn_cancel_timermusic);
        lv_timermusic = (ListView) findViewById(R.id.lv_timermusic);
        rl_timermusic_none = (RelativeLayout) findViewById(R.id.rl_timer_bottom_music_none);
        checkBox = (CheckBox) rl_timermusic_none.findViewById(R.id.cb_phonemusic_none);
        tv_music_vibrator = (TextView) findViewById(R.id.tv_music_vibrator);
    }

    //获取铃声选项名称
    public String getMusicType() {
        String musicType;
        if (TimerMusicAdapter.temp == -1)
            musicType = "停止播放";
        else
            musicType = listRong.get(TimerMusicAdapter.temp);
        return musicType;
    }

    //返回铃声list选项
    public static List<String> getListMusic() {
        return listRong;
    }
    //listview点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //检测是否点击同一个选项 暂停音乐和停止振动
        isPlaying = !isPlaying;
        if (TimerMusicAdapter.temp == position && !isPlaying) {
            SoundUtils.pauseRing();
            VibratorUtils.virateCancle(TimerMusic.this);
            return;
        }
        //获取temp位置 刷新控件
        TimerMusicAdapter.temp = position;
        adapter.notifyDataSetChanged();
        checkBox.setChecked(false);//设置按钮无的状态
        //开始振动
        VibratorUtils.vibrate(TimerMusic.this, Global.getTimerVibrator, 1);
        //获取铃声选项
        ringtone = listRingtones.get(position);
        Log.i("Uri", ringtone + "");
        //开始播放音乐
        SoundUtils.playRing(TimerMusic.this, ringtone);
    }

    //添加点击事件
    @Override
    public void onClick(View v) {
        pauseRing();
        switch (v.getId()) {
            case R.id.btn_set_timermusic:
                MusicType();
                break;
            case R.id.btn_cancel_timermusic:
                //返回按钮 返回选项事件
                pauseRing();
                finish();
                break;
            case R.id.rl_timer_bottom_music_none:
                //点击按钮无选项的状态
                TimerMusicAdapter.temp = -1;
                adapter.notifyDataSetChanged();
                checkBox.setChecked(true);
                break;
        }
    }
    //返回选中音乐名称
    private void MusicType() {
        String music = getMusicType();
        Intent intent = new Intent();
        intent.putExtra("music", music);
        setResult(84, intent);
        finish();
    }
    //回退键
    @Override
    public void onBackPressed() {
        pauseRing();
        super.onBackPressed();
    }
    //暂停音乐和停止振动
    private void pauseRing() {
        VibratorUtils.virateCancle(TimerMusic.this);
        SoundUtils.pauseRing();
    }
}
