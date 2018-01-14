package com.example.asd.clock.Fragment;


import android.view.View;

import com.example.asd.clock.Fragment.Base.BaseFragment;


/**
 * Created by asd on 2017/12/27.
 */
//闹钟界面
public class ClockFragment extends BaseFragment {

    @Override
    public View baseView(View view) {
        title.setText("闹钟");
        return view;
    }
}
