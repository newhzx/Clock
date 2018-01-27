package com.example.asd.clock.Fragment;


import android.view.View;

import com.example.asd.clock.Fragment.Base.BaseFragment;

//秒表界面
public class StopWatchFragment extends BaseFragment {
    @Override
    public View baseView(View view) {
        title.setText("秒表");
        edit.setVisibility(View.INVISIBLE);
        add.setVisibility(View.INVISIBLE);
        return view;
    }
}
