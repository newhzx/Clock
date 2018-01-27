package com.example.asd.clock.Clock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.asd.clock.R;

public class AddClockTag extends Activity {
    private EditText tag;//标签
    private Button btn_addclocktag;//返回标签按钮
    public static String tags = "闹钟";//初始标签初始值
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addclocktag);
        initView();//初始化控件
    }

    private void initView() {
        //初始化标签控件
        tag = (EditText)findViewById(R.id.et_addclock_tag);
        tag.setText(getTags());//设置标签控件值
        tag.setSelection(tag.getText().toString().length());//移动光标
        btn_addclocktag = (Button) findViewById(R.id.btn_addclocktag);//返回按钮初始化
        //点击回调事件
        btn_addclocktag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tags = tag.getText().toString();
                setTags(tags);
                Intent intent = new Intent();
                intent.putExtra("tags", tags);
                setResult(22, intent);
                finish();
            }
        });
    }
    //返回标签和设置标签
    public static String getTags(){
        return tags;
    }
    public static void setTags(String tags){
        AddClockTag.tags = tags;
    }
}
