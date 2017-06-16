package com.sengled.media.player.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bjbj.slsijk.player.AVOptions;
import com.bjbj.slsijk.player.SLSMediaPlayer;
import com.bjbj.slsijk.player.widget.SLSVideoTextureView;
import com.sengled.media.player.R;
import com.sengled.media.player.event.PlaybackEvent;
import com.sengled.media.player.task.RequestPlaybackMarkTask;
import com.sengled.media.player.task.RequestPlaybackMotionTask;
import com.sengled.media.player.task.RequestPlaybackVideoTask;
import com.sengled.media.player.widget.SengledVideoIndicator;
import com.sengled.media.player.widget.timeaxis.AxisMotion;
import com.sengled.media.player.widget.timeaxis.AxisVideo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.views.ExpCalendarView;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

/**
 * Created by admin on 2017/4/13.
 */
public class AboutFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RequestPlaybackMarkTask task = new RequestPlaybackMarkTask();
        task.execute();
    }

    private TextView curYM;
    private ExpCalendarView expCalendarView;
    private SLSVideoTextureView playView;
    private SengledVideoIndicator videoIndicator;
    private View mLoadingView;
    private ImageButton playOrPauseBtn;
    private TextView timeLengthView;

    private List<AxisVideo> playbackList;
    private AxisVideo curAxisVideo; // 当前播放视频对象

    private void initVideoPlayer(SLSVideoTextureView videoView){
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);
        videoView.setAVOptions(options);
        videoView.setDisplayAspectRatio(SLSVideoTextureView.ASPECT_RATIO_16_9);

        videoView.setBufferingIndicator(mLoadingView);
        videoView.setOnCompletionListener(new PlayCompletionListener());
        videoView.setOnPreparedListener(new PlayPreparedListener());
        videoView.setOnSeekCompleteListener(new PlaySeekCompletionListener());
        videoView.setOnTouchListener(new PlayTouchListener());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_video_playback_layout,container, false);

        initLayout(view);
        initVideoPlayer(playView); // 初始化播放器
        initEvent();
        return view;
    }

    private void initLayout(View view){
        expCalendarView = (ExpCalendarView) view.findViewById(R.id.calendar_exp);
        curYM = (TextView) view.findViewById(R.id.media_video_playback_curym);
        videoIndicator = (SengledVideoIndicator)view.findViewById(R.id.media_video_axis_indicator);
        playView = (SLSVideoTextureView)view.findViewById(R.id.list_item_video_view);
        mLoadingView = view.findViewById(R.id.item_loading_view);
        timeLengthView =(TextView) view.findViewById(R.id.play_time_length);
        playOrPauseBtn = (ImageButton) view.findViewById(R.id.play_or_pause_btn);
        videoIndicator.setmPlayer(playView);

        CellConfig.Month2WeekPos = CellConfig.middlePosition;
        CellConfig.ifMonth = false;
        expCalendarView.shrink();
    }

    private void initEvent(){
        expCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                MarkedDates markedDates = expCalendarView.getMarkedDates();
                markedDates.remove(date);
                for (DateData dateData : markedDates.getAll()) {
                    if (dateData.getMarkStyle().getStyle() == MarkStyle.BACKGROUND){
                        markedDates.remove(dateData);
                    }
                }
                expCalendarView.markDate(date.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Color.parseColor("#FBC19B"))));
                curYM.setText(String.format("%s年%s月",date.getYear(),date.getMonth()));

                new RequestPlaybackVideoTask(getContext()).execute();
                new RequestPlaybackMotionTask(getContext()).execute();
            }
        });

        videoIndicator.setMotionClickListener(new SengledVideoIndicator.MotionClickListener() {
            @Override
            public void motionClick(AxisMotion axisMotion) {
                Toast.makeText(getContext(), axisMotion.getTimePoint().toString(),Toast.LENGTH_LONG).show();
            }
        });

        videoIndicator.setDotMoveListener(new SengledVideoIndicator.ProgressMoveListener() {
            @Override
            public void dotMove(AxisVideo axisVideo, final Long point) {
                playVideo(axisVideo);

                Runnable seekTask = new Runnable() {
                    @Override
                    public void run() {
                        if (playView.isPlaying()){
                            playView.pause();
                        }
                        playView.seekTo(point*1000);
                    }
                };
                new Handler().postDelayed(seekTask, 200);
            }
        });

        playOrPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playbackList ==null || playbackList.isEmpty()){
                    Toast.makeText(getContext(),R.string.playback_list_is_empty,Toast.LENGTH_SHORT).show();
                    return;
                }

                if (playView.isPlaying()){
                    playOrPauseBtn.setBackgroundResource(R.mipmap.video_play_btn);
                    playView.pause();
                    videoIndicator.setStepping(false);
                }else {
                    playOrPauseBtn.setBackgroundResource(R.mipmap.video_stop_btn);
                    playView.start();
                    videoIndicator.setStepping(true);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateDateMark(PlaybackEvent.UpdateMarkEvent event) throws ParseException {
        List<String> dataList = event.dateList;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        DateData markDate = null;
        for (String s : dataList) {
            calendar.setTime(sdf.parse(s));
            markDate = new DateData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
            expCalendarView.markDate(markDate.setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.GREEN)));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateAxisVideo(PlaybackEvent.UpdateAxisVideoEvent event){
        videoIndicator.setVideoBeanData(event.axisVideos);
        playbackList = event.axisVideos;
        playVideo(null);
    }

    /**
     * 播放下一个视频，如果刚开始，播放第一个
     */
    private void playVideo(AxisVideo willVideo){
        if(playbackList ==null || playbackList.isEmpty()){
            Toast.makeText(getContext(),R.string.playback_list_is_empty,Toast.LENGTH_SHORT).show();
            return;
        }

        if (willVideo != null){
            curAxisVideo = willVideo;

        }else if (curAxisVideo == null){
            curAxisVideo = playbackList.get(0);

        }else {
            int index = playbackList.indexOf(curAxisVideo);
            if (index == playbackList.size()-1){
                curAxisVideo = playbackList.get(0);
            }else {
                curAxisVideo = playbackList.get(index+1);
            }
        }
        playView.stopPlayback();
        playView.setVideoPath(curAxisVideo.getVideoPath());
        videoIndicator.jumpToNextVideo(curAxisVideo);
    }

    private static final int UPDATE_CURRENT_TIME = 0x01;
    private Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (playView.getCurrentPosition() < playView.getDuration()){
                String totalLength = generateTime(playView.getDuration());
                String curLength = generateTime(playView.getCurrentPosition());
                timeLengthView.setText(curLength+"/"+totalLength);
                sendEmptyMessageDelayed(UPDATE_CURRENT_TIME, 1000);
            }
        }
    };

    private String generateTime(long position) {
        int totalSeconds = (int) (position / 1000);

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes,
                    seconds).toString();
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds)
                    .toString();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateAxisVideo(PlaybackEvent.UpdateAxisMotionEvent event){
        videoIndicator.setMotionBeanList(event.axisMotions);
    }

    class PlayCompletionListener implements SLSMediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(SLSMediaPlayer slsMediaPlayer) {
            playVideo(null);
        }
    }

    class  PlayPreparedListener implements SLSMediaPlayer.OnPreparedListener{
        @Override
        public void onPrepared(SLSMediaPlayer slsMediaPlayer) {
            timeHandler.sendEmptyMessageDelayed(UPDATE_CURRENT_TIME,1000);
            playOrPauseBtn.setVisibility(View.GONE);
            videoIndicator.setStepping(true);
        }
    }

    class PlaySeekCompletionListener implements  SLSMediaPlayer.OnSeekCompleteListener{

        @Override
        public void onSeekComplete(SLSMediaPlayer slsMediaPlayer) {
            playView.start();
            videoIndicator.setStepping(true);
        }
    }

    class PlayTouchListener implements View.OnTouchListener{

        private static final int SHOW_OR_HIDE = 0x11;
        private Handler sHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                playOrPauseBtn.setVisibility(View.GONE);
            }
        };

        @Override
        public boolean onTouch(View v, MotionEvent event)  {
            if (playView.isPlaying()){
                playOrPauseBtn.setBackgroundResource(R.mipmap.video_stop_btn);
            }else {
                playOrPauseBtn.setBackgroundResource(R.mipmap.video_play_btn);
            }

            if (playOrPauseBtn.isShown()){
                sHandler.removeMessages(SHOW_OR_HIDE);
                sHandler.sendEmptyMessageDelayed(SHOW_OR_HIDE, 3000);
            }else {
                playOrPauseBtn.setVisibility(View.VISIBLE);
            }
            return true;
        }
    }
}
