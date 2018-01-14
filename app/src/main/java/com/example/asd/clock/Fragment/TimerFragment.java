package com.example.asd.clock.Fragment;


import android.view.View;

import com.example.asd.clock.Fragment.Base.BaseFragment;


/**
 * Created by asd on 2017/12/27.
 */
//计时器界面
public class TimerFragment extends BaseFragment {

    @Override
    public View baseView(View view) {
        title.setText("计时器");
        edit.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        return view;
    }
}
