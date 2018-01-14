package com.example.asd.clock.WorldClock;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.asd.clock.Fragment.Adapter.WorldClockAdapter;
//listview自定义移动类
public class DragAndDrapListView extends ListView {
    private int dragViewId;//
    private boolean isDragAndDrop = false;//是否编辑状态
    private WindowManager.LayoutParams windowParams;//窗口参数
    private WindowManager windowManager;//窗口管理类
    private ImageView dragImageView;//存放临时图像
    private int offsetScreenTop; //距离屏幕顶部的位置
    private int offsetViewTop;  //手指按下位置距离item顶部的位置
    private int dragPosition;//拖动位置
    private int srcY; //用于判断滑动方向
    private boolean mIsLongPressed = false;

    public DragAndDrapListView(Context context) {
        super(context);
    }
    public DragAndDrapListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //设置拖动控件
    public void setDragViewId(int dragViewId) {
        this.dragViewId = dragViewId;
    }

    //触摸事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int mLastMotionX = 0;
        int mLastMotionY = 0;
        long lastDownTime = 0;
        switch (ev.getAction()) {
            //不是编辑作态返回
            case MotionEvent.ACTION_DOWN:
                if (!isDragAndDrop) {
                    break;
                }
                //记录按下xy坐标位置和事件
                mLastMotionX = (int) ev.getX();
                mLastMotionY = (int) ev.getY();
                lastDownTime = ev.getDownTime();
                break;
            case MotionEvent.ACTION_MOVE:
                //求出触摸事件状态
                if(!mIsLongPressed){
                    int thisx = (int) ev.getX();
                    int thisy = (int) ev.getY();
                    long eventTime = ev.getEventTime();
                    mIsLongPressed = isLongPressed(mLastMotionX, mLastMotionY, thisx, thisy, lastDownTime, eventTime, 500);
                }
                //如果是长按事件 则返回false
                if (mIsLongPressed) {
                    return false;
                } else {
                    //不是长按事件 则返回true并挪动item
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    int rawY = (int) ev.getRawY();
                    int currentPostion = dragPosition = pointToPosition(x, y);
                    if (currentPostion == AdapterView.INVALID_POSITION) {
                        return super.onInterceptTouchEvent(ev);
                    }
                    //getChildAt是获取可见位置的item
                    ViewGroup itemView = (ViewGroup) getChildAt(currentPostion - getFirstVisiblePosition());
                    offsetScreenTop = rawY - y;
                    offsetViewTop = y - itemView.getTop();
                    View dragger = itemView.findViewById(dragViewId);//获取控件view
                    if (dragger != null && x > dragger.getLeft()) {
                        itemView.setDrawingCacheEnabled(true);// 开启cache.
                        Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());// 根据cache创建一个新的bitmap对象.
                        itemView.setDrawingCacheEnabled(false);// 一定关闭cache，否则复用会出现错乱
                        startDrag(bm, y);
                    }
                    return true;
                }
                //按钮弹起 把mIsLongPressed重置
            case MotionEvent.ACTION_UP:
                mIsLongPressed = false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
    //通过xy坐标和时间差 判断长按事件状态
    static boolean isLongPressed(float lastX, float lastY, float thisX,
                                 float thisY, long lastDownTime, long thisEventTime,
                                 long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        //长按事件
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            return true;
        }
//        非长按事件
        return false;
    }
    //触摸事件实现
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (dragImageView != null) {
            int y = (int) ev.getY();//记录y坐标
            int startY = 0;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY = (int) ev.getY();
                    srcY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    onDrag(y);//被选中的item
                    getChildAt(dragPosition - getFirstVisiblePosition()).setVisibility(View.INVISIBLE);
                    break;
                case MotionEvent.ACTION_UP:
                    stopDrag();
                    int endY = (int) ev.getY();
                    if (Math.abs(endY - startY) < 6) {
                        return false;// 距离较小，当作click事件来处理
                    }
                    getChildAt(dragPosition - getFirstVisiblePosition()).setVisibility(View.VISIBLE);
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }
    //开始拖动
    private void startDrag(Bitmap bm, int y) {
        windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = y - offsetViewTop + offsetScreenTop;
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 不需获取焦点
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 不需接受触摸事件
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON// 保持设备常开，并保持亮度不变。
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;// 窗口占满整个屏幕，忽略周围的装饰边框（例如状态栏）。此窗口需考虑到装饰边框的内容。

        // windowParams.format = PixelFormat.TRANSLUCENT;// 默认为不透明，这里设成透明效果.
        windowParams.windowAnimations = 0;// 窗口所使用的动画设置
        //产生的影像
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(bm);
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(imageView, windowParams);
        dragImageView = imageView;
    }
//  拖动影像实现方法
    private void onDrag(int y) {
        int offsetTop = y - offsetViewTop; //顶部不能出界
        if (dragImageView != null && offsetTop >= 0 && offsetTop <= getChildAt(getChildCount() - 1).getTop()) {
            windowParams.alpha = 0.8f;// 透明度
            windowParams.y = y - offsetViewTop + offsetScreenTop;// 移动y值.//记得要加上dragOffset，windowManager计算的是整个屏幕.(标题栏和状态栏都要算上)
            windowManager.updateViewLayout(dragImageView, windowParams);// 时时移动.
        }
        onChange(y);
        scrollListView(y);
    }

    //同步滑动ListView
    private void scrollListView(int y) {
        View view = getChildAt(dragPosition - getFirstVisiblePosition());
        int offsetY = srcY - y;

        if (y < getHeight() / 3 && y < srcY) { //listview向上滑
            setSelectionFromTop(dragPosition, offsetY + view.getTop());
        } else if (y > getHeight() / 3 * 2 && y > srcY) { //listview向下滑
            setSelectionFromTop(dragPosition, offsetY + view.getTop());
        }
        srcY = y;
    }
    //同步改变item的位置
    private void onChange(int y) {
        int currentPostion = pointToPosition(0, y);
        if (currentPostion == AdapterView.INVALID_POSITION) {
            currentPostion = dragPosition;
        }
        if (dragPosition != currentPostion) {
            WorldClockAdapter adapter = (WorldClockAdapter) getAdapter();
            adapter.change(dragPosition, currentPostion);
            swich(dragPosition, currentPostion);
        }
        dragPosition = currentPostion;
    }

    //切换隐藏的位置
    private void swich(int start, int end) {
        getChildAt(start - getFirstVisiblePosition()).setVisibility(View.VISIBLE);
        getChildAt(end - getFirstVisiblePosition()).setVisibility(View.INVISIBLE);
    }

    //停止拖动，删除影像
    public void stopDrag() {
        if (dragImageView != null) {
            windowManager.removeView(dragImageView);
            dragImageView = null;
        }
    }
    //设置编辑和完成状态
    public void setIsDragAndDrop(boolean isDragAndDrop) {
        this.isDragAndDrop = isDragAndDrop;
    }

}
