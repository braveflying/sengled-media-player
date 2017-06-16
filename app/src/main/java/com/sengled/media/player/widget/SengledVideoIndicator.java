package com.sengled.media.player.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by admin on 2017/5/31.
 */
public class SengledVideoIndicator extends View {

    private static final int NO_VIDEO_ITEM_HEIGHT = 30; // 默认每个时间点的高度
    private static final int VIDEO_ITEM_HEIGHT = 300;  // 有视频数据是的高度
    private static final int AXIS_START_Y = 30; //时间轴 Y抽起点
    private static final int AXIS_START_X = 250; //时间轴 Y抽起点
    private static final int AXIS_DOT_LENGTH = 30; // 时间点的横线长度
    private static final int AXIS_MOTION_LENGTH = 15;

    private static final Long MOTION_INTERVAL_SECOND = 300L; // motion的间隔时间

    private static int total_height = 0; //时间轴的总高度
    private static int total_width = 0;

    private List<AxisVideo> videoBeanList = null;
    private List<AxisMotion> axisMotionList = null;

    private List<Integer> timePoint = new ArrayList<>(); //包含视频的时间点

    private Paint linePaint;
    private Paint textPaint;
    private Paint bgPain;
    private Paint motionPaint;
    private Paint progressDotPaint;

    private  LinkedList<AxisMotion> motionPoint; // motion 显示出来的点
    private LinkedList<RectF> motionRectf = new LinkedList<>(); // motion 点显示的坐标范围

    private LinkedList<RectF> videoRectF = new LinkedList<>(); //视频坐标范围

    private RectF progressRect; //进度点的Rect
    private boolean progressMovable = true;
    private boolean progressSelected = false;

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


    private void init(){
        setDrawingCacheEnabled(true);
        initEnlarge();
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

        progressDotPaint = new Paint();
        progressDotPaint.setAntiAlias(true);
        progressDotPaint.setStyle(Paint.Style.FILL);
        progressDotPaint.setColor(Color.parseColor("#EE82EE"));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //setMeasuredDimension(total_width, total_height + 100);
        drawBackground(canvas);
        drawMarkDot(canvas);
        drawProgressDot(canvas); //画进度显示点

        drawMotionDot(canvas);

        synProgressDot();// 进度点

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        total_width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(total_width, total_height + 100);
    }

    private void measureVideoDate(){
        Collections.sort(videoBeanList, new Comparator<AxisVideo>() {
            @Override
            public int compare(AxisVideo o1, AxisVideo o2) {
                return o1.getStartTime().before(o2.getStartTime())?1:0;
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
                timePoint.add(vStartCalendar.get(Calendar.HOUR_OF_DAY));
                vStartCalendar.add(Calendar.HOUR_OF_DAY,1);
            }while (vStartCalendar.before(vEndCalendar));
        }

        total_height= timePoint.size() * VIDEO_ITEM_HEIGHT + (24 - timePoint.size())*NO_VIDEO_ITEM_HEIGHT;
    }

    private void drawMarkDot(Canvas canvas){
        if (videoBeanList.isEmpty()){
            return;
        }
        int pointY = AXIS_START_Y;
        for (int i = 0; i <= 23; i++) {

            canvas.drawLine(AXIS_START_X, pointY, AXIS_START_X+AXIS_DOT_LENGTH, pointY, linePaint);
            canvas.drawText(i+":00",AXIS_START_X+AXIS_DOT_LENGTH+10, pointY+10, textPaint);
            if (timePoint.contains(i)){
                pointY += VIDEO_ITEM_HEIGHT;
            }else{
                pointY += NO_VIDEO_ITEM_HEIGHT;
            }
        }

        textPaint.setTextSize(25);
        textPaint.setColor(Color.parseColor("#cccccc"));
        canvas.drawText("0:00",AXIS_START_X+AXIS_DOT_LENGTH+10, pointY+10, textPaint);
        canvas.drawLine(AXIS_START_X+AXIS_DOT_LENGTH/2, AXIS_START_Y, AXIS_START_X+AXIS_DOT_LENGTH/2, total_height+AXIS_START_Y, linePaint);
    }

    private void drawProgressDot(Canvas canvas){
        if (progressRect == null) {
            progressRect = new RectF(AXIS_START_X, AXIS_START_Y - 15, AXIS_START_X + 30, AXIS_START_Y + 15);
        }
        canvas.drawOval(progressRect, progressDotPaint);

    }

    private void drawBackground(Canvas canvas){
        videoRectF.clear();
        for (AxisVideo bean : videoBeanList) {
            int startY = calcTopOffsetByDate(bean.getStartTime());
            int endY = calcTopOffsetByDate(bean.getEndTime());

            RectF bgRect = new RectF(0,startY, total_width, endY);
            videoRectF.add(bgRect);
            canvas.drawRect(bgRect, bgPain);
        }
    }

    private void drawMotionDot(Canvas canvas) {
        motionPoint = new LinkedList<>();
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
            RectF motionRect = new RectF(widthStart ,startY, widthStart+AXIS_MOTION_LENGTH, endY);
            motionRectf.add(motionRect);
            canvas.drawRoundRect(motionRect, 8, 8, motionPaint);
        }
    }

    /**
     * 根据时间计算距离远点的Y 距离
     * @param date
     * @return
     */
    private int calcTopOffsetByDate(Date date){
        Calendar paramCalendar = Calendar.getInstance();
        paramCalendar.setTime(date);

        int hour = paramCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = paramCalendar.get(Calendar.MINUTE);
        int second = paramCalendar.get(Calendar.SECOND);

        int height = AXIS_START_Y;
        for (int i = 0; i < hour; i++) {
            if (timePoint.contains(i)){
                height += VIDEO_ITEM_HEIGHT;
            }else {
                height += NO_VIDEO_ITEM_HEIGHT;
            }
        }

        int smallSecond = minute*60 + second;
        long hourSecond=3600; // 一小时总秒数
        float oneSecondHeight = (float) VIDEO_ITEM_HEIGHT/(float) hourSecond;

        height += smallSecond * oneSecondHeight;
        return height;
    }

    private MotionClickListener motionClickListener;
    private ProgressMoveListener dotMoveListener;

    public void setMotionClickListener(MotionClickListener motionClickListener) {
        this.motionClickListener = motionClickListener;
    }

    public void setDotMoveListener(ProgressMoveListener dotMoveListener) {
        this.dotMoveListener = dotMoveListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                motionClick(event);
                progressUpEvent();
                break;
            case MotionEvent.ACTION_DOWN:
                progressDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                progressMoveEvent(event);
                break;
        }
        enlargeView(event);
        return true;
    }

    private EnlargeView enlargeView;
    private PopupWindow enlargeWin;

    //初始化放大镜
    private void initEnlarge(){
        enlargeView = new EnlargeView(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(200, 200);
        enlargeView.setLayoutParams(layoutParams);
        enlargeWin = new PopupWindow(enlargeView, ViewGroup.LayoutParams.MATCH_PARENT, 200);
        //enlargeWin.setBackgroundDrawable(new ColorDrawable(Color.RED));
    }

    private void enlargeView(MotionEvent event){
        if (progressSelected) {
            float offsetY = progressRect.centerY()-event.getY()-100;

            if (!enlargeWin.isShowing()) {
                //enlargeWin.showAsDropDown(this, (int) progressRect.centerX() / 2, (int) (progressRect.centerY()-this.getHeight()));
                enlargeWin.showAtLocation(this,Gravity.TOP, (int) progressRect.centerX() / 2, (int)(event.getRawY()+offsetY));
            } else {
                //enlargeWin.update((int) progressRect.centerX() / 2, (int) (progressRect.centerY()-this.getHeight()), ViewGroup.LayoutParams.MATCH_PARENT, 200);
                enlargeWin.update((int) progressRect.centerX() / 2, (int)(event.getRawY()+offsetY), ViewGroup.LayoutParams.MATCH_PARENT, 200);
            }
            enlargeView.startEnlarge(getBitmap(), progressRect.centerX(), progressRect.centerY());
        }else {
            enlargeWin.dismiss();
        }
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        int width = getRight() - getLeft();
        int height = getBottom() - getTop();
        final boolean opaque = getDrawingCacheBackgroundColor() != 0 || isOpaque();
        Bitmap.Config quality;
        if (!opaque) {
            switch (getDrawingCacheQuality()) {
                case DRAWING_CACHE_QUALITY_AUTO:
                case DRAWING_CACHE_QUALITY_LOW:
                case DRAWING_CACHE_QUALITY_HIGH:
                default:
                    quality = Bitmap.Config.ARGB_8888;
                    break;
            }
        } else {
            quality = Bitmap.Config.RGB_565;
        }
        if (opaque) bitmap.setHasAlpha(false);
        bitmap = Bitmap.createBitmap(getResources().getDisplayMetrics(),
                width, height, quality);
        bitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
        boolean clear = getDrawingCacheBackgroundColor() != 0;
        Canvas canvas = new Canvas(bitmap);
        if (clear) {
            bitmap.eraseColor(getDrawingCacheBackgroundColor());
        }
        computeScroll();
        final int restoreCount = canvas.save();
        canvas.translate(-getScrollX(), -getScrollY());
        draw(canvas);
        canvas.restoreToCount(restoreCount);
        canvas.setBitmap(null);
        return bitmap;
    }

    /**
     * 跳转到下个视频的开始位置
     * @param nextVideo
     */
    public boolean jumpToNextVideo(AxisVideo nextVideo){
        if (nextVideo == null){
            return false;
        }

        curPlayVideo = nextVideo;
        RectF temp = video2Rect(nextVideo);
        if (temp == null){
            return false;
        }
        progressMove(temp.top);
        return true;
    };

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

    private boolean progressMove(float posY){
        if (progressMovable){
            if (progressRect.centerY() < AXIS_START_Y && posY<AXIS_START_Y){
                return false;
            }
            if (progressRect.centerY() > (total_height+AXIS_START_Y) && posY>(total_height+AXIS_START_Y)){
                return false;
            }

            Rect oldRect = new Rect();
            progressRect.roundOut(oldRect);

            progressRect.top = posY-15;
            progressRect.bottom = posY+15;

            Rect newRect = new Rect();
            progressRect.roundOut(newRect);

            oldRect.union(newRect);
            invalidate(oldRect);
            return false;
        }
        return true;
    }

    private void progressUpEvent(){
        if (progressSelected){
            fireVideoListener();
        }
        progressSelected = false;
        stepping = true;
    }

    private void progressMoveEvent(MotionEvent event){
        if (progressSelected){
            progressMove(event.getY());
        }
    }

    private void fireVideoListener(){
        if (dotMoveListener != null && progressMovable){
            for (int i = 0; i < videoRectF.size(); i++) {
                if (videoRectF.get(i).contains(progressRect.centerX(), progressRect.centerY())){

                    long offset = posOffset(videoRectF.get(i), progressRect.centerY());

                    dotMoveListener.dotMove(videoBeanList.get(i), offset);
                    return;
                }
            }
        }
    }

    /**
     * 获得seek dot对应的Video Rect
     * @return
     */
    private RectF getDotTheBackRect(){
        for (RectF rectF : videoRectF) {
            if (rectF.contains(progressRect.centerX(), progressRect.centerY())){
                return rectF;
            }
        }
        return null;
    }


    private  long posOffset(RectF vRect, float pos){
        float distance = pos - vRect.top;
        long hourSecond=3600; // 一小时总秒数
        float unitPixTime = (float) hourSecond/(float) VIDEO_ITEM_HEIGHT;
        return new BigDecimal(distance*unitPixTime).longValue();
    };

    private boolean progressDown(MotionEvent event){
        RectF tempRect = new RectF(progressRect.left-10, progressRect.top -10, progressRect.right +10, progressRect.bottom+10);
        if (tempRect.contains(event.getX(),event.getY())){
            progressSelected = true;
            stepping = false;
            getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
        return true;
    }

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
    private static final int UPDATE_DOT=0;
    private boolean stepping = false; // 进度点是否前进
    private SLSVideoTextureView mPlayer;
    private AxisVideo curPlayVideo;

    public void setmPlayer(SLSVideoTextureView mPlayer) {
        this.mPlayer = mPlayer;
    }

    public void setStepping(boolean stepping) {
        this.stepping = stepping;
    }

    @SuppressLint("HandlerLeak")
    private Handler mProgressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (!stepping){
                msg = obtainMessage(UPDATE_DOT);
                sendMessageDelayed(msg, 1000);
                return;
            }

            setProgress();

            System.out.println("yangyonghui: "+progressRect.top);
            msg = obtainMessage(UPDATE_DOT);
            sendMessageDelayed(msg, 1000);
        }
    };
    private void synProgressDot(){
        //getParent().requestDisallowInterceptTouchEvent(false);
        mProgressHandler.removeMessages(UPDATE_DOT);
        mProgressHandler.sendEmptyMessageDelayed(UPDATE_DOT, 1000);
    }

    private long setProgress() {
        if (mPlayer == null || progressSelected)
            return 0;

        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();

        /*RectF rectF = getDotTheBackRect();
        if (rectF == null){
            jumpToVideo();
            return position;
        }*/

        if (mPlayer.isPlaying() && duration > 0 && curPlayVideo!=null) {
            //AxisVideo video = rect2Video(rectF);
            Calendar curPlayCalendar = Calendar.getInstance();
            curPlayCalendar.setTime(curPlayVideo.getStartTime());
            curPlayCalendar.add(Calendar.MILLISECOND, (int) position);
            int pos = calcTopOffsetByDate(curPlayCalendar.getTime());
            progressMove(pos);
        }

        return position;
    }

    /**
     * 如果当期进度点在无视频区域，跳转到附近的视频起点
     */
    private void jumpToVideo(){
        if (videoBeanList==null || videoBeanList.isEmpty()){
            return;
        }
        float distance = Float.MAX_VALUE;
        RectF minRectF = null;
        for (RectF rectF : videoRectF) {
            if (distance == Float.MAX_VALUE){
                distance = Math.abs(progressRect.centerY() - rectF.top);
                minRectF = rectF;
            }else if (Math.abs(progressRect.centerY()-rectF.top) < distance){
                distance = Math.abs(progressRect.centerY() -rectF.top);
                minRectF = rectF;
            }
        }

        RectF goRectF = null;
        if (progressRect.centerY() < minRectF.top){
            goRectF = minRectF;
        }else if (progressRect.centerY() >= minRectF.bottom && videoRectF.getLast() != minRectF){
            goRectF = videoRectF.get(videoRectF.indexOf(minRectF)+1);
        }else {
            goRectF = videoRectF.getFirst();
        }

        progressMove(goRectF.top);
        fireVideoListener();
    }

    private AxisVideo rect2Video(RectF vRect){
       return videoBeanList.get(videoRectF.indexOf(vRect));
    }

    public static interface MotionClickListener{
        void motionClick(AxisMotion axisMotion);
    }

    public static interface ProgressMoveListener{
        void dotMove(AxisVideo axisVideo, Long point);
    }
}
