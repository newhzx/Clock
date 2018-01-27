package com.example.asd.clock.Clock;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asd.clock.R;

import java.util.List;

public class AddClockMusicAdapter extends BaseAdapter {
    public List<String> listMusic;//铃声名称集合
    private Activity activity;//成员变量
    public static int temp = 0;//初始位置
    //构造方法
    public AddClockMusicAdapter(Activity activity,List<String> listMusic) {
        this.listMusic = listMusic;
        this.activity = activity;
        /*for (int i =0;i<listMusic.size();i++){
            Log.i("listMusic",listMusic.get(i));
        }*/
    }

 /*   public static List<String> getListMusic() {
        listMusic = new ArrayList<String>();
        String[] strs = Global.getPhoneMusic;
        for (int i = 0; i < strs.length; i++) {
            listMusic.add(strs[i]);
        }
        return listMusic;
    }*/

    @Override
    public int getCount() {
        return listMusic.size();
    }

    @Override
    public Object getItem(int position) {
        return listMusic.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler viewHodler = null;
        //初始化ViewHolder里面的控件
        if (convertView == null) {
            viewHodler = new ViewHodler();
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_phonemusic, null);
            viewHodler.tv_PhoneMusic = (TextView) convertView.findViewById(R.id.tv_phonemusic);
            viewHodler.cb_PhoneMusic = (CheckBox) convertView.findViewById(R.id.cb_phonemusic);
            viewHodler.rl_addclock_bottom_music_choose = (RelativeLayout) convertView.findViewById(R.id.rl_addclock_bottom_music_choose);
            convertView.setTag(viewHodler);
        } else {
            viewHodler = (ViewHodler) convertView.getTag();
        }
        //获取铃声名称
        String str = getItem(position).toString();
        //设置铃声名称
        viewHodler.tv_PhoneMusic.setText(str);
        //设置是否选中状态
        if(temp == position){
            viewHodler.cb_PhoneMusic.setChecked(true);
        }else{
            viewHodler.cb_PhoneMusic.setChecked(false);
        }
        return convertView;
    }
    //viewholder
    class ViewHodler {
        CheckBox cb_PhoneMusic;
        TextView tv_PhoneMusic;
        RelativeLayout rl_addclock_bottom_music_choose;
    }
}
