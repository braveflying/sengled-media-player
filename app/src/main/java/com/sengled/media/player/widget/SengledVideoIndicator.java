package com.sengled.media.player.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bjbj.slsijk.player.MediaPlayHelper;
import com.bjbj.slsijk.player.widget.SLSVideoTextureView;
import com.sengled.media.player.R;
import com.sengled.media.player.widget.timeaxis.AxisMotion;
import com.sengled.media.player.widget.timeaxis.AxisVideo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/5/31.
 */
public class SengledVideoIndicator extends View {

    private static final int NO_VIDEO_ITEM_HEIGHT = 30; // 默认每个时间点的高度
    private static final int VIDEO_ITEM_HEIGHT = 300;  // 有视频数据是的高度

    private static final int AXIS_START_X = 250; //时间轴 Y抽起点
    private static final int AXIS_DOT_LENGTH = 30; // 时间点的横线长度
    private static final int AXIS_MOTION_LENGTH = 15;

    private static final Long MOTION_INTERVAL_SECOND = 300L; // motion的间隔时间

    private static int AXIS_START_Y = 30; //时间轴 Y抽起点
    private static int total_height = 0; //时间轴的总高度
    private static int total_width = 0;

    private List<AxisVideo> videoBeanList = null;
    private List<AxisMotion> axisMotionList = null;

    private Set<String> timePoint = new HashSet<>(); //包含视频的时间点
    private LinkedHashMap<String, Integer> dotTextMap = new LinkedHashMap<>(); // 字符串时间文字,及高度
    private MediaPlayHelper playHelper;

    private Paint linePaint,textPaint,bgPain,motionPaint,footPaint;
    private View referView; // 播放线
    private View mLoadingView; // 视频加载或切换中的视图

    private  LinkedList<AxisMotion> motionPoint; // motion 显示出来的点
    private LinkedList<RectF> motionRectf = new LinkedList<>(); // motion 点显示的坐标范围
    private Map<String, RectF> timeRectMap = new HashMap<>(); //每个时间点对应的坐标范围
    private LinkedList<RectF> videoRectF = new LinkedList<>(); //视频坐标范围

    public SengledVideoIndicator(Context context) {
        super(context);
        init();
    }

    public SengledVideoIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setVideoBeanData(List<AxisVideo> videoBeanList){
        this.videoBeanList = videoBeanList;
        measureVideoDate();
        requestLayout();
        invalidate();
    }

    public void setMotionBeanList(List<AxisMotion> axisMotionList){
        this.axisMotionList = axisMotionList;
        invalidate();
    }

    public void setPlayHelper(MediaPlayHelper playHelper) {
        this.playHelper = playHelper;
    }

    public void setReferView(View referView) {
        this.referView = referView;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)referView.getLayoutParams();
        this.AXIS_START_Y = params.topMargin+params.height/2;
        requestLayout();
    }

    public void setmLoadingView(View mLoadingView) {
        this.mLoadingView = mLoadingView;
    }

    private void init(){
        videoBeanList = new ArrayList<>(); //初始化数据集合
        axisMotionList = new ArrayList<>();

        linePaint = new Paint();  // 初始化画笔
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.parseColor("#ffaa76"));
        linePaint.setStrokeWidth(5);
        linePaint.setStyle(Paint.Style.FILL);

        textPaint= new Paint();
        textPaint.setAntiAlias(true);
        //textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(30);

        bgPain = new Paint();
        bgPain.setAntiAlias(true);
        bgPain.setStyle(Paint.Style.FILL);
        bgPain.setColor(Color.parseColor("#fce3c3"));

        motionPaint = new Paint();
        motionPaint.setAntiAlias(true);
        motionPaint.setStyle(Paint.Style.FILL);
        motionPaint.setColor(Color.RED);

        footPaint = new Paint();
        footPaint.setAntiAlias(true);
        footPaint.setStyle(Paint.Style.FILL);
        footPaint.setTextSize(50);
        footPaint.setColor(Color.parseColor("#0099CC"));

        scrollToPos(); //同步滚动scrollView
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //setMeasuredDimension(total_width, total_height + 100);
        drawBackground(canvas);
        drawMarkDot(canvas);

        drawMotionDot(canvas);

        drawFooter(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        total_width = MeasureSpec.getSize(widthMeasureSpec);
        if (referView.getParent() instanceof View){
            View parent = (View) referView.getParent();
            setMeasuredDimension(total_width, total_height+parent.getHeight());
            System.out.println("parent height===" + referView.getHeight());
            System.out.println("parent height===" + parent.getHeight());
        }else {
            setMeasuredDimension(total_width, total_height);
        }
    }

    private void measureVideoDate(){
        total_height = 0;
        Collections.sort(videoBeanList, new Comparator<AxisVideo>() {
            @Override
            public int compare(AxisVideo o1, AxisVideo o2) {
                return o1.getStartTime().before(o2.getStartTime())?1:-1;
            }
        });
        timePoint.clear();

        //计算有视频的时间点数
        Calendar vStartCalendar = Calendar.getInstance();
        Calendar vEndCalendar = Calendar.getInstance();
        for (AxisVideo AxisVideo : videoBeanList) {
            vStartCalendar.setTime(AxisVideo.getStartTime());
            vStartCalendar.set(Calendar.MINUTE,0);
            vStartCalendar.set(Calendar.SECOND,0);
            vEndCalendar.setTime(AxisVideo.getEndTime());

            do{
                timePoint.add(String.format("%d%d",vStartCalendar.get(Calendar.DAY_OF_MONTH),vStartCalendar.get(Calendar.HOUR_OF_DAY)));
                vStartCalendar.add(Calendar.HOUR_OF_DAY,1);
            }while (vStartCalendar.before(vEndCalendar));
        }

        if (!videoBeanList.isEmpty()){
            AxisVideo lastAxisVideo = videoBeanList.get(0);
            vStartCalendar.setTime(lastAxisVideo.getStartTime());
            vEndCalendar.setTime(lastAxisVideo.getEndTime());
            vStartCalendar.set(Calendar.HOUR_OF_DAY, 0);
            vStartCalendar.set(Calendar.MINUTE, 0);
            vStartCalendar.set(Calendar.SECOND, 0);

            timeRectMap.clear();
            dotTextMap.clear();
            int dotIndex = AXIS_START_Y;
            while (vEndCalendar.after(vStartCalendar)){
                int startY = dotIndex;
                if (timePoint.contains(String.format("%d%s",vEndCalendar.get(Calendar.DAY_OF_MONTH), vEndCalendar.get(Calendar.HOUR_OF_DAY)))){
                    dotIndex += VIDEO_ITEM_HEIGHT;
                }else {
                    dotIndex += NO_VIDEO_ITEM_HEIGHT;
                }
                String key = String.format("%d-%d:00",vEndCalendar.get(Calendar.DAY_OF_MONTH),vEndCalendar.get(Calendar.HOUR_OF_DAY));
                dotTextMap.put(key,dotIndex);
                timeRectMap.put(key, new RectF(0,startY, total_width, dotIndex));

                System.out.println("yyh time"+vEndCalendar.getTime().toString());
                vEndCalendar.add(Calendar.HOUR_OF_DAY,-1);
            }
            total_height = dotIndex - AXIS_START_Y;
        }
    }

    private void drawMarkDot(Canvas canvas){
        if (videoBeanList.isEmpty()){
            return;
        }

        textPaint.setTextSize(25);
        textPaint.setColor(Color.parseColor("#cccccc"));
        for (Map.Entry<String, Integer> dotEntry : dotTextMap.entrySet()) {
            canvas.drawLine(AXIS_START_X, dotEntry.getValue(), AXIS_START_X+AXIS_DOT_LENGTH, dotEntry.getValue(), linePaint);
            canvas.drawText(dotEntry.getKey(),AXIS_START_X+AXIS_DOT_LENGTH+10, dotEntry.getValue()+10, textPaint);
        }

        canvas.drawLine(AXIS_START_X+AXIS_DOT_LENGTH/2, AXIS_START_Y, AXIS_START_X+AXIS_DOT_LENGTH/2, total_height+AXIS_START_Y, linePaint);
    }


    private void drawBackground(Canvas canvas){
        videoRectF.clear();
        for (AxisVideo bean : videoBeanList) {
            Date startTime = bean.getStartTime();
            Date endTime = bean.getEndTime();
            int startY = calcTopOffsetByDate(startTime);
            int endY = calcTopOffsetByDate(endTime);

            RectF bgRect = new RectF(0,endY, total_width, startY);
            videoRectF.add(bgRect);
            canvas.drawRect(bgRect, bgPain);
        }
    }

    private void drawMotionDot(Canvas canvas) {
        motionPoint = new LinkedList<>();
        Collections.sort(axisMotionList, new Comparator<AxisMotion>() {
            @Override
            public int compare(AxisMotion o1, AxisMotion o2) {
                return o1.getTimePoint().after(o2.getTimePoint())?1:-1;
            }
        });

        for (AxisMotion axisMotion : axisMotionList) {
            if (motionPoint.isEmpty()) {
                axisMotion.setEndPoint(axisMotion.getTimePoint());
                motionPoint.add(axisMotion);
            } else {
                AxisMotion lastMotionPoint = motionPoint.getLast();
                if ((axisMotion.getTimePoint().getTime() - lastMotionPoint.getEndPoint().getTime()) <= (MOTION_INTERVAL_SECOND * 1000)
                        && lastMotionPoint.getZone() == axisMotion.getZone()) {
                    lastMotionPoint.setEndPoint(axisMotion.getTimePoint());
                } else {
                    axisMotion.setEndPoint(axisMotion.getTimePoint());
                    motionPoint.add(axisMotion);
                }
            }
        }

        for (AxisMotion axisMotion : motionPoint) {
            int startY = calcTopOffsetByDate(axisMotion.getTimePoint());
            int endY = calcTopOffsetByDate(axisMotion.getEndPoint());

            int widthStart = total_width/3;
            switch (axisMotion.getZone()){
                case zone1:
                    motionPaint.setColor(Color.RED);
                    widthStart +=100;
                    break;
                case zone2:
                    motionPaint.setColor(Color.GREEN);
                    widthStart +=300;
                    break;
                case zone3:
                    motionPaint.setColor(Color.BLUE);
                    widthStart +=500;
                    break;

            }
            RectF motionRect = new RectF(widthStart ,endY, widthStart+AXIS_MOTION_LENGTH, startY);
            motionRectf.add(motionRect);
            canvas.drawRoundRect(motionRect, 8, 8, motionPaint);
        }
    }

    private void drawFooter(Canvas canvas){
        canvas.drawLine(0,AXIS_START_Y+total_height+100,total_width,AXIS_START_Y+total_height+100, footPaint);


        float startPosY = AXIS_START_Y+total_height+200;
        RectF roundRect = new RectF(0, startPosY,total_width,startPosY+120);
        canvas.drawRoundRect(roundRect, 20,20, footPaint);
        canvas.save();

        Paint paint = new Paint(footPaint);
        paint.setColor(Color.parseColor("#ffffff"));
        String text = getResources().getString(R.string.load_before_day_playback_data);
        float textWidth = footPaint.measureText(text);
        float startPosX = (total_width-textWidth)/2;
        canvas.drawText(text, startPosX, startPosY+80, paint);
    }

    /**
     * 根据时间计算距离远点的Y 距离
     * @param date
     * @return
     */
    private int calcTopOffsetByDate(Date date){
        Calendar paramCalendar = Calendar.getInstance();
        paramCalendar.setTime(date);

        String key = String.format("%d-%d:00",paramCalendar.get(Calendar.DAY_OF_MONTH),paramCalendar.get(Calendar.HOUR_OF_DAY));

        int hour = paramCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = paramCalendar.get(Calendar.MINUTE);
        int second = paramCalendar.get(Calendar.SECOND);

        int height = 0;
        if (dotTextMap.get(key) != null){
            height = dotTextMap.get(key);
        }


        int smallSecond = minute*60 + second;
        long hourSecond=3600; // 一小时总秒数
        float oneSecondHeight = (float) VIDEO_ITEM_HEIGHT/(float) hourSecond;

        height -= smallSecond * oneSecondHeight;
        return height;
    }

    private MotionClickListener motionClickListener;
    public void setMotionClickListener(MotionClickListener motionClickListener) {
        this.motionClickListener = motionClickListener;
    }

    private void scrollToPos(){
        scrollHandler.removeMessages(SCROLL_POS);
        scrollHandler.sendEmptyMessageDelayed(SCROLL_POS, 1000);
    }

    private static final int SCROLL_POS = 0x01;
    private boolean isAutoCall = false;
    private long seekToSecond = 0; // seek到当前视频的秒数

    public void setAutoCall(boolean autoCall) {
        isAutoCall = autoCall;
    }

    private Handler scrollHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (isAutoCall && mPlayer.isPlaying()) {
                RectF curRect = video2Rect(curPlayVideo);

                long playSecond = seekToSecond + (mPlayer.getCurrentPosition()/1000);

                float offsetY = (float)VIDEO_ITEM_HEIGHT/(float) 3600 * (float) playSecond;
                int scrollY = new BigDecimal(curRect.bottom-offsetY-referView.getTop() - referView.getHeight()/2).intValue();

                System.out.println("scrollHandler=============  "+scrollY);
                scrollView.scrollTo(0, scrollY);
            }
            scrollHandler.sendEmptyMessageDelayed(SCROLL_POS, 1000);
        }
    };


    private Handler seekHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            seekToSecond = (Long) msg.obj;
            //mPlayer.seekTo(seekToSecond*1000);
            //String url = String.format("%s&start=%s",curPlayVideo.getVideoPath(),seekToSecond);
            //mPlayer.setVideoPath(url);
            Calendar seekDate = Calendar.getInstance();
            seekDate.setTime(curPlayVideo.getStartTime());
            seekDate.add(Calendar.SECOND, (int) seekToSecond);
            playHelper.startPlayback(seekDate.getTime());
        }
    };
    /**
     * 同步
     */
    public void synProgress(int scrollY){
        if (isAutoCall){
            return;
        }

        for (RectF rectF : videoRectF) {
            int referY = scrollY+referView.getTop() + referView.getHeight()/2;
            if (rectF.contains(0, referY)){
                long seekPos = posOffset(rectF, referY);

                AxisVideo playVideo = rect2Video(rectF);
                if (playVideo != curPlayVideo){
                    playVideo(playVideo);
                }
                System.out.println("synProgress=============  "+seekPos);
                Message msg = Message.obtain();
                msg.obj = seekPos;
                seekHandler.sendMessage(msg);
                break;
            }else {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isAutoCall = true;
                    }
                },3000);
            }
        }
    }

    /**
     * 播放下一个视频，如果刚开始，播放第一个
     */
    public void playVideo(AxisVideo willVideo){
        if(videoBeanList ==null || videoBeanList.isEmpty()){
            Toast.makeText(getContext(), R.string.playback_list_is_empty,Toast.LENGTH_SHORT).show();
            return;
        }

        if (willVideo != null && curPlayVideo == willVideo){
            Toast.makeText(getContext(), "Playing...",Toast.LENGTH_SHORT).show();
            return;
        }

        if (willVideo != null){
            curPlayVideo = willVideo;

        }else if (curPlayVideo == null){
            curPlayVideo = videoBeanList.get(0);

        }else {
            int index = videoBeanList.indexOf(curPlayVideo);
            if (index == videoBeanList.size()-1){
                curPlayVideo = videoBeanList.get(0);
            }else {
                curPlayVideo = videoBeanList.get(index+1);
            }
        }

        if (mLoadingView != null){
            mLoadingView.setVisibility(VISIBLE);
        }
        seekToSecond = 0;
        mPlayer.stopPlayback();
        mPlayer.setVideoPath(curPlayVideo.getVideoPath());
    }

    public long getVideoLength(){
        return curPlayVideo.getEndTime().getTime()-curPlayVideo.getStartTime().getTime();
    }

    public long getCurPosition(){
        return seekToSecond*1000 + mPlayer.getCurrentPosition();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                motionClick(event);
                break;
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
        }
        return true;
    }

    private void actionDown(MotionEvent event){
        isAutoCall = false;
    }

    /**
     * 通过AxisVideo对象获得对应的RectF
     * @param nextVideo
     * @return
     */
    private RectF video2Rect(AxisVideo nextVideo){
        if (videoRectF==null || videoRectF.isEmpty()){
            return null;
        }
        return videoRectF.get(videoBeanList.indexOf(nextVideo));
    }


    /**
     * 把在矩阵中偏移的位置转换成播放器 seek的时间点
     * @param vRect 当前播放矩阵
     * @param pos 在矩阵中偏移的位置
     * @return
     */
    private  long posOffset(RectF vRect, float pos){
        float distance = vRect.bottom-pos;
        long hourSecond=3600; // 一小时总秒数
        float unitPixTime = (float) hourSecond/(float) VIDEO_ITEM_HEIGHT;
        return new BigDecimal(distance*unitPixTime).longValue();
    };

    /**
     * scroll y 坐标转换成时间点
     * @param coordinateY
     * @return
     */
    public String coordinateY2Time(int coordinateY){
        String hours = "";
        RectF timeRect = null;
        float baseY = coordinateY+referView.getTop() + referView.getHeight()/2;
        for (Map.Entry<String, RectF> integerRectFEntry : timeRectMap.entrySet()) {
            if (integerRectFEntry.getValue().contains(0, baseY)){
                hours = integerRectFEntry.getKey();
                timeRect = integerRectFEntry.getValue();
                break;
            }
        }
        if (timeRect == null){
            return "";
        }

        float unitSecHeight = timeRect.height()/(float) 3600;
        int totalSeconds = new BigDecimal((timeRect.bottom - baseY)/ (float) unitSecHeight).intValue();

        String[] times = hours.split("-|:");

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        return String.format(Locale.US, "%02d:%02d:%02d", Integer.parseInt(times[1]), minutes,
                seconds).toString();
    };


    private boolean motionClick(MotionEvent event){
        RectF vRect = null;
        RectF mRect = null;
        for (int i = 0; i < motionRectf.size(); i++) {
            boolean flag = motionRectf.get(i).contains(event.getX(), event.getY());
            if (flag){
                mRect = motionRectf.get(i);
                break;
            }
        }
        if (mRect == null){
            return false;
        }

        for (RectF rectF : videoRectF) {
            if (rectF.contains(mRect)){
                vRect = rectF;
                break;
            }
        }

        if (mRect != null && vRect != null) {
            long pos = posOffset(vRect, mRect.top);
            mPlayer.seekTo(pos * 1000);
        }
        return true;
    }

    /**
     * 进度条更新
     */
    private SLSVideoTextureView mPlayer;
    private AxisVideo curPlayVideo;
    private ScrollView scrollView;

    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    public void setmPlayer(SLSVideoTextureView mPlayer) {
        this.mPlayer = mPlayer;
    }


    private AxisVideo rect2Video(RectF vRect){
       return videoBeanList.get(videoRectF.indexOf(vRect));
    }

    public static interface MotionClickListener{
        void motionClick(AxisMotion axisMotion);
    }
}
