package com.example.asd.clock.Fragment;


import android.content.Intent;
import android.view.View;

import com.example.asd.clock.Clock.AddClock;
import com.example.asd.clock.Fragment.Base.BaseFragment;

//闹钟界面
public class ClockFragment extends BaseFragment {

    @Override
    public View baseView(View view) {
        title.setText("闹钟");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(activity, AddClock.class), 20);
            }
        });
        return view;
    }
}
