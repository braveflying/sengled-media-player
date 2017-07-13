package com.sengled.media.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class SegScrollView extends ScrollView {

    private static final String TAG = "SegScrollView";

    private OnScrollStatusListener onScrollStatusListener;

    //Runnable延迟执行的时间
    private long delayMillis = 10;

    //上次滑动的时间
    private long lastScrollUpdate = -1;

    private Runnable scrollerTask = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastScrollUpdate) > 10) {
                lastScrollUpdate = -1;
                onScrollStatusListener.onScrollEnd(getScrollY());
            } else {
                postDelayed(this, delayMillis);
            }
        }
    };

    public SegScrollView(Context context) {
        this(context, null);
    }

    public SegScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SegScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        onScrollStatusListener.onScrollChanged(l, t, oldl, oldt);
        if (lastScrollUpdate == -1) {
            onScrollStatusListener.onScrollStart();
            postDelayed(scrollerTask, delayMillis);
        }
        // 更新ScrollView的滑动时间
        lastScrollUpdate = System.currentTimeMillis();
    }

    public void setOnScrollStatusListener(SegScrollView.OnScrollStatusListener listener){
        onScrollStatusListener = listener;
    }

    public interface OnScrollStatusListener{

        //滑动开始
        public void onScrollStart();

        //滑动结束
        public void onScrollEnd(int scrollY);

        public void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}