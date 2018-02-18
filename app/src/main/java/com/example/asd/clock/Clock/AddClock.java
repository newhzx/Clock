package com.example.asd.clock.Clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asd.clock.Clock.SwitchButton.SwitchButton;
import com.example.asd.clock.Fragment.Adapter.ClockAdapter;
import com.example.asd.clock.Fragment.Bean.Clock;
import com.example.asd.clock.R;
import com.example.asd.clock.Utils.ClockXMLUtils;
import com.example.asd.clock.Utils.Global;
import com.example.asd.clock.Utils.InitClockData;
import com.example.asd.clock.Utils.SoundUtils;
import com.example.asd.clock.Utils.Utils;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import org.dom4j.DocumentException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.asd.clock.Fragment.Adapter.ClockAdapter.activity;

public class AddClock extends AppCompatActivity {
    private SoundPool soundPool;//声音池
    private Button cancel, add,btn_delete_addclock;//取消和添加按钮
    private List<String> list, listLunch, listHour, listMinute;//时间滚轮集合
    private LoopView loopViewline, loopViewlines, loopViewLunch, loopViewHour, loopViewMinute;//时间滚轮
    private SwitchButton mSB;//开关 稍后提醒
    private RelativeLayout rl_repeat, rl_tag, rl_tips, rl_music;//选择栏
    private TextView tv_repeat, tv_tag, tv_music;//显示栏——重复,标签，铃声
    private Clock clock;
    private boolean isEdit;
    private Intent intent;
    private int position;
    private AlarmManager alarmManager;
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addclock);
        //声音池——初始化 加载 播放
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundPool.load(AddClock.this, R.raw.collide, 1);
        soundPool.play(1, 1, 1, 0, 0, 2);
        initView();//初始化控件
        initLoopView();
        intent = getIntent();
        isEdit = intent.getBooleanExtra("isEdit",false);
        clock = (Clock) intent.getSerializableExtra("json");
        position = intent.getIntExtra("position",0);
        if (isEdit){
            readData(clock);
        }else{
            initData();//初始化数据
        }
        initListener();//初始化侦听事件
    }

    private void readData(final Clock clock) {
        Log.i("clocks",clock.getId()+"");
        loopViewLunch.setCurrentPosition(clock.getLunchSelect());
        int hourSelect = clock.getHourSelect();
        Log.i("hourSelect",hourSelect+"");
        loopViewHour.setCurrentPosition(hourSelect);//初始化时位置
        loopViewMinute.setCurrentPosition(clock.getMinuteSelect());//初始化分位置

        Map<Integer,Boolean> map = Utils.getRepeat(clock.getJson());
        String clockRepeat = Utils.getRepeatContent(map);
        AddClockRepeatAdapter.setSelector(map);
        if("".equals(clockRepeat)) clockRepeat = "永不";
        tv_repeat.setText(clockRepeat);

        AddClockTag.setTags(clock.getTags());
        tv_tag.setText(clock.getTags());

        btn_delete_addclock.setVisibility(View.VISIBLE);
        btn_delete_addclock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int removeId = clock.getId();
                removeItem(removeId);
                ClockAdapter.listChoose.remove(position);
                try {
                    new ClockAdapter().initSwitch(ClockAdapter.listChoose);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                setResult(20);
                finish();
            }
        });
    }
    private void removeItem(int id) {
        ClockXMLUtils.removeNoteInXML(id);
    }

    private void initListener() {
        //滚轮添加滑动侦听事件——上下午 时 分
        loopViewLunch.setListener(new OnItemSelected());
        loopViewHour.setListener(new OnItemSelected());
        loopViewMinute.setListener(new OnItemSelected());
        //取消和添加按钮
        cancel.setOnClickListener(new OnClick());
        add.setOnClickListener(new OnClick());
        //切换开关添加侦听事件
        mSB.setOnCheckedChangeListener(new OnCheckChange());
        //选项添加点击事件
        rl_repeat.setOnClickListener(new OnClick());
        rl_tag.setOnClickListener(new OnClick());
        rl_tips.setOnClickListener(new OnClick());
        rl_music.setOnClickListener(new OnClick());
    }

    private void initView() {
        //初始化滚轮
        loopViewline = (LoopView) findViewById(R.id.loopViewline);
        loopViewlines = (LoopView) findViewById(R.id.loopViewlines);
        loopViewLunch = (LoopView) findViewById(R.id.loopViewLunch);
        loopViewHour = (LoopView) findViewById(R.id.loopViewHour);
        loopViewMinute = (LoopView) findViewById(R.id.loopViewMinute);
        initColorAndLine();//初始化颜色
        //取消和添加按钮
        cancel = (Button) findViewById(R.id.cancel);
        add = (Button) findViewById(R.id.add);
        //
        mSB = (SwitchButton) findViewById(R.id.sb_ios);
        mSB.setChecked(true);

        rl_repeat = (RelativeLayout) findViewById(R.id.rl_addclock_bottom_repeat);
        rl_tag = (RelativeLayout) findViewById(R.id.rl_addclock_bottom_tag);
        rl_tips = (RelativeLayout) findViewById(R.id.rl_addclock_bottom_tips);
        rl_music = (RelativeLayout) findViewById(R.id.rl_addclock_bottom_music);

        tv_repeat = (TextView) findViewById(R.id.tv_repeat);
        tv_tag = (TextView) findViewById(R.id.tv_tag);
        tv_music = (TextView) findViewById(R.id.tv_music);

        btn_delete_addclock = (Button)findViewById(R.id.btn_delete_addclock);
    }

    private void initColorAndLine() {
        //添加两侧滚轮分割线颜色
        loopViewline.setDividerColor(Color.parseColor("#bb666666"));
        loopViewlines.setDividerColor(Color.parseColor("#bb666666"));
        //添加上下午滚轮分割线 字体大小 颜色
        loopViewLunch.setDividerColor(Color.parseColor("#bb666666"));
        loopViewLunch.setOuterTextColor(Color.parseColor("#bb666666"));
        loopViewLunch.setCenterTextColor(Color.parseColor("#ffffff"));
        loopViewLunch.setTextSize(18);
        //添加时滚轮分割线 字体大小 颜色
        loopViewHour.setDividerColor(Color.parseColor("#bb666666"));
        loopViewHour.setOuterTextColor(Color.parseColor("#bb666666"));
        loopViewHour.setCenterTextColor(Color.parseColor("#ffffff"));
        loopViewHour.setTextSize(18);
        //添加分滚轮分割线 字体大小 颜色
        loopViewMinute.setDividerColor(Color.parseColor("#bb666666"));
        loopViewMinute.setOuterTextColor(Color.parseColor("#bb666666"));
        loopViewMinute.setCenterTextColor(Color.parseColor("#ffffff"));
        loopViewMinute.setTextSize(18);
    }
    private  void initLoopView(){
        //滚轮上下午—时—分—集合初始化
        list = new ArrayList<String>();
        listLunch = new ArrayList<String>();
        listHour = new ArrayList<String>();
        listMinute = new ArrayList<String>();
        //滚轮填充数据
        list.add("");
        listLunch = InitClockData.getLoopViewLunch(listLunch);
        loopViewLunch.setNotLoop();
        listHour = InitClockData.getLoopViewHour(listHour);
        listMinute = InitClockData.getLoopViewMinute(listMinute);

        //设置原始数据
        loopViewline.setItems(list);
        loopViewLunch.setItems(listLunch);
        loopViewHour.setItems(listHour);
        loopViewMinute.setItems(listMinute);
        loopViewlines.setItems(list);
    }
    private void initData() {
        //设置初始化位置
        Calendar calendar = Calendar.getInstance();//时钟类
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//时
        int minute = calendar.get(Calendar.MINUTE);//分
        if (hour < 12) {
            loopViewLunch.setCurrentPosition(0);//上下午
        } else if (11 < hour && hour < 24) {
            loopViewLunch.setCurrentPosition(1);//上下午
        }
        while (hour > 12) hour = hour - 12;
        if (hour == 0) hour = 12;
        loopViewHour.setCurrentPosition(hour);//初始化时位置
        loopViewMinute.setCurrentPosition(minute);//初始化分位置
        //初始化铃声的名称
        int index = AddClockMusicAdapter.temp;
        SoundUtils.initMusic(AddClock.this);
        if (index != -1) {
            tv_music.setText(SoundUtils.getListRong().get(index));
        } else if (index == -1) {
            tv_music.setText("无");
        }
    }
    //回调设置选项数据
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //重复设置选项
        if (resultCode == 21) tv_repeat.setText(data.getStringExtra("repeat"));
        //标签设置选项
        if (resultCode == 22) tv_tag.setText(data.getStringExtra("tags"));
        //铃声设置选项
        if (resultCode == 23) tv_music.setText(data.getStringExtra("music"));
    }

    //滚轮侦听事件 选择时候有声音
    class OnItemSelected implements OnItemSelectedListener {
        public void onItemSelected(int index) {
            soundPool.load(AddClock.this, R.raw.collide, 1);
            soundPool.play(1, 1, 1, 0, 0, 2);
        }
    }
    //点击侦听事件
    class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //取消按钮
                case R.id.cancel:
                    //初始化重复选项
                    AddClockRepeatAdapter.setSelector(AddClockRepeatAdapter.initMap());
                    //设置闹钟初始化
                    AddClockTag.setTags("闹钟");
                    finish();
                    break;
                case R.id.add:
                    if(isEdit){
                        removeItem(clock.getId());
                    }
                    addClock();
                    break;
                case R.id.rl_addclock_bottom_repeat:
                    //点击重复回调方法
                    startActivityForResult(new Intent(AddClock.this, AddClockRepeat.class), 21);
                    break;
                case R.id.rl_addclock_bottom_tag:
                    //点击标签回调方法
                    startActivityForResult(new Intent(AddClock.this, AddClockTag.class), 22);
                    break;
                case R.id.rl_addclock_bottom_music:
                    //点击铃声回调方法
                    startActivityForResult(new Intent(AddClock.this, AddClockMusic.class), 23);
                    break;
            }
        }
    }
    private void addClock(){
        //存储数据
        try {
            addClockToXml();
            //初始化重复选项
            AddClockRepeatAdapter.setSelector(AddClockRepeatAdapter.initMap());
            //设置闹钟初始化
            AddClockTag.setTags("闹钟");
            setResult(20);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void addClockToXml() throws JSONException, IOException, DocumentException {
        int lunchSelect = loopViewLunch.getSelectedItem();
        int hourSelect = loopViewHour.getSelectedItem();
        int minuteSelect = loopViewMinute.getSelectedItem();
        Calendar calendar = Calendar.getInstance();//获取系统时钟类
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//时
        int minute = calendar.get(Calendar.MINUTE);//分
        int second = calendar.get(Calendar.SECOND);//秒
        int lunch = 0;
        if (hour < 12) {
            lunch = 0;
        } else if (11 < hour && hour < 24) {
            lunch = 1;
        }
        while (hour >= 12) hour = hour - 12;
        if(minute!=minuteSelect||lunch!=lunchSelect||hour!=hourSelect){
            second = 0;
        }
        //id的参数
        int id;String hours = hourSelect+"",minutes = minuteSelect+"",seconds = second+"";
        if (hourSelect<10) hours = "0"+hourSelect;
        if(minuteSelect<10) minutes = "0"+minuteSelect;
        if(second<10) seconds = "0"+second;
        id = Integer.valueOf(lunchSelect+""+hours+""+minutes+""+seconds);
        String tags = AddClockTag.tags;
        String json = map2Json(AddClockRepeatAdapter.getSelected()).toString();
        System.out.println(id+"!"+lunch+"!"+hour+"!"+minute+"!"+second+"!"+tags+"!"+json);
        int index = ClockXMLUtils.addNoteToClockXML(id,lunchSelect,hourSelect,minuteSelect,second,tags,json);
        if(index == -1){
            Utils.showToast(AddClock.this,"该闹钟已经存在");
            return;
        }
        ClockAdapter.listChoose.add(index,Boolean.TRUE);
        new ClockAdapter().initSwitch(ClockAdapter.listChoose);

        if(alarmManager==null){
            alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        }
        Intent intent = new Intent(activity,AlarmClockReceiver.class);
        intent.setAction(Global.Action+"."+id);//设置action 区别别的广播
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstTime = SystemClock.elapsedRealtime();
        long systemTime = System.currentTimeMillis();//系统时间
        Calendar c = Calendar.getInstance();
        // 根据用户选择时间来设置Calendar对象
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        Log.i("AlarmTest",hour+":"+minute+":"+second);
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
    }
    public static JSONObject map2Json(Map<Integer,Boolean> map) throws JSONException {
        JSONObject json = new JSONObject();
        Set<Integer> set = map.keySet();
        for (Iterator<Integer> it = set.iterator(); it.hasNext();) {
            Integer key = it.next();
            json.put(String.valueOf(key),Boolean.valueOf(map.get(key)));
        }
        return json;
    }

    //侦听开关选项按钮
    class OnCheckChange implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Utils.showToast(AddClock.this, "true");
            } else {
                Utils.showToast(AddClock.this, "false");
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
            //初始化重复选项
            AddClockRepeatAdapter.setSelector(AddClockRepeatAdapter.initMap());
            //设置闹钟初始化
            AddClockTag.setTags("闹钟");
            finish();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {

        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
        }
        return super.onKeyDown(keyCode, event);
    }
}
