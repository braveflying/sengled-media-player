package com.sengled.media.player.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bjbj.slsijk.player.AVOptions;
import com.bjbj.slsijk.player.SLSMediaPlayer;
import com.bjbj.slsijk.player.widget.SLSVideoTextureView;
import com.sengled.media.player.R;
import com.sengled.media.player.adapter.PlaybackControlAdapter;
import com.sengled.media.player.event.PlaybackEvent;
import com.sengled.media.player.task.RequestPlaybackMarkTask;
import com.sengled.media.player.task.RequestPlaybackMotionTask;
import com.sengled.media.player.task.RequestPlaybackVideoTask;
import com.sengled.media.player.widget.SegScrollView;
import com.sengled.media.player.widget.SengledVideoIndicator;
import com.sengled.media.player.widget.timeaxis.AxisMotion;
import com.sengled.media.player.widget.timeaxis.AxisVideo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.views.ExpCalendarView;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

/**
 * Created by admin on 2017/6/26.
 */
public class VideoPlaybackNewActivity extends AppCompatActivity {

    private TextView curYM;
    private ExpCalendarView expCalendarView;
    private SLSVideoTextureView playView;
    private View mLoadingView;
    private ImageButton playOrPauseBtn;
    private TextView timeLengthView;
    private TextSwitcher textSwitcher;

    private TextView countInfoView;
    private List<AxisVideo> playbackList = new ArrayList<>();
    private View playReferLine;
    private RecyclerView controlView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PlaybackControlAdapter mPlaybackControlAdapter;

    private String token; // 设备token
    private String checkDate; //当前选择的回放日期

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        setContentView(R.layout.media_video_playback_new_layout);
        initToolbar();

        initLayout();
        initBaseLineTextSwitcher();
        initVideoPlayer(playView); // 初始化播放器
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar now = Calendar.getInstance();
        DateData curDateData = new DateData(now.get(Calendar.YEAR), now.get(Calendar.MONTH)+1,now.get(Calendar.DAY_OF_MONTH));
        requestBackendData(curDateData);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RequestPlaybackMarkTask task = new RequestPlaybackMarkTask();
        task.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        playView.stopPlayback();
        playView.releaseSurfactexture();
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.media_drawer_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("视频回放");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initLayout(){
        expCalendarView = (ExpCalendarView) findViewById(R.id.calendar_exp);
        curYM = (TextView) findViewById(R.id.media_video_playback_curym);
        playView = (SLSVideoTextureView)findViewById(R.id.list_item_video_view);
        mLoadingView = findViewById(R.id.item_loading_view);
        timeLengthView =(TextView) findViewById(R.id.play_time_length);
        playOrPauseBtn = (ImageButton) findViewById(R.id.play_or_pause_btn);
        playReferLine =  findViewById(R.id.play_refer_line);
        textSwitcher = (TextSwitcher) findViewById(R.id.media_baseline_text);
        countInfoView = (TextView) findViewById(R.id.media_video_playback_count_info);
        controlView = (RecyclerView) findViewById(R.id.media_playback_control_view);
        mLayoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL,false);

        mPlaybackControlAdapter = new PlaybackControlAdapter(this, playbackList);
        controlView.setLayoutManager(mLayoutManager);
        controlView.setAdapter(mPlaybackControlAdapter);

        CellConfig.Month2WeekPos = CellConfig.middlePosition;
        CellConfig.ifMonth = false;
        expCalendarView.shrink();
    }

    private void initEvent(){
        expCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                requestBackendData(date);
            }
        });


        playOrPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playbackList ==null || playbackList.isEmpty()){
                    Toast.makeText(getApplication(),R.string.playback_list_is_empty,Toast.LENGTH_SHORT).show();
                    return;
                }

                if (playView.isPlaying()){
                    playOrPauseBtn.setBackgroundResource(R.mipmap.video_play_btn);
                    playView.pause();
                }else {
                    playOrPauseBtn.setBackgroundResource(R.mipmap.video_stop_btn);
                    playView.start();
                }
            }
        });
    }

    private void requestBackendData(DateData date){
        MarkedDates markedDates = expCalendarView.getMarkedDates();

        Set<DateData> removeSet = new HashSet<>();
        for (DateData dateData : markedDates.getAll()) {
            if (dateData.getMarkStyle().getStyle() == MarkStyle.BACKGROUND){
                removeSet.add(dateData);
            }
        }
        for (DateData dateData : removeSet) {
            markedDates.remove(dateData);
        }

        expCalendarView.markDate(date.setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Color.parseColor("#FBC19B"))));
        curYM.setText(String.format("%s年%s月",date.getYear(),date.getMonth()));

        playView.stopPlayback();

        token = getIntent().getStringExtra("token");
        checkDate = String.format("%d-%s-%s",date.getYear(),date.getMonthString(),date.getDayString());

        new RequestPlaybackVideoTask(getApplication()).execute(token,checkDate);
        new RequestPlaybackMotionTask(getApplication()).execute();
    }

    private void initBaseLineTextSwitcher(){
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView showText = new TextView(getApplicationContext());
                showText.setTextSize(10);
                showText.setTextColor(Color.GRAY);
                showText.setText("00:00:00");
                return showText;
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
        countInfoView.setText(String.format("%d video files", event.axisVideos.size()));
        playbackList.clear();
        playbackList.addAll(event.axisVideos);
        mPlaybackControlAdapter.notifyDataSetChanged();
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
    }

    class PlayCompletionListener implements SLSMediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(SLSMediaPlayer slsMediaPlayer) {
        }
    }

    class  PlayPreparedListener implements SLSMediaPlayer.OnPreparedListener{
        @Override
        public void onPrepared(SLSMediaPlayer slsMediaPlayer) {
            timeHandler.removeMessages(UPDATE_CURRENT_TIME);
            timeHandler.sendEmptyMessageDelayed(UPDATE_CURRENT_TIME,1000);
            playOrPauseBtn.setVisibility(View.GONE);
        }
    }

    class PlaySeekCompletionListener implements  SLSMediaPlayer.OnSeekCompleteListener{

        @Override
        public void onSeekComplete(SLSMediaPlayer slsMediaPlayer) {
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
