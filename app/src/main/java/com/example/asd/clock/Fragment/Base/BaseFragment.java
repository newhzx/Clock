package com.example.asd.clock.Fragment.Base;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.asd.clock.R;

import org.dom4j.DocumentException;

import java.io.IOException;


/**
 * Created by asd on 2017/12/27.
 */
//实例化基本界面 包括顶部和内容
public abstract  class BaseFragment extends Fragment {
    public Activity activity;//
    public View mRootView;//布局界面
    public TextView title;//标题
    public Button edit,add;//编辑和添加按钮
    public FrameLayout basefragment;//内容fargment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = getActivity();
    }
    //实例化内容页面
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            return initView();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //初始化顶部的标题和编辑添加按钮 以及内容fragment
    public View initView() throws DocumentException, IOException {
//        绘制顶部和内容
        mRootView = View.inflate(activity, R.layout.basepager,null);
        //1.顶部控件
        title = (TextView) mRootView.findViewById(R.id.titleview);
        edit = (Button)mRootView.findViewById(R.id.editview);
        add = (Button)mRootView.findViewById(R.id.addview);
        //2.内容控件
        basefragment = (FrameLayout) mRootView.findViewById(R.id.basefragment);
        //返回该控件
        return baseView(mRootView);
    }
    //抽象方法 必须实现该View 把内容呈现出来
    public abstract View baseView(View view) throws DocumentException, IOException;
}
