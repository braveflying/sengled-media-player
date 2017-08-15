package com.sengled.media.player.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bjbj.slsijk.player.AVOptions;
import com.bjbj.slsijk.player.MediaPlayHelper;
import com.bjbj.slsijk.player.SLSMediaPlayer;
import com.bjbj.slsijk.player.common.MediaPlayerDto;
import com.bjbj.slsijk.player.widget.SLSVideoTextureView;
import com.sengled.media.player.R;
import com.sengled.media.player.common.Const;
import com.sengled.media.player.event.PlaybackEvent;
import com.sengled.media.player.task.RequestPlaybackMarkTask;
import com.sengled.media.player.task.RequestPlaybackMotionTask;
import com.sengled.media.player.widget.ObservableScrollView;
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
import java.util.Collection;
import java.util.Date;
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
public class VideoPlaybackActivity extends AppCompatActivity {

    private TextView curYM;
    private ExpCalendarView expCalendarView;
    private SLSVideoTextureView playView;
    private SengledVideoIndicator videoIndicator;
    private View mLoadingView;
    private ImageButton playOrPauseBtn;
    private TextView timeLengthView;
    private TextSwitcher textSwitcher;

    private TextView countInfoView;
    private List<AxisVideo> playbackList;
    private View playReferLine;
    private ObservableScrollView scrollView;

    private String token; // 设备token
    private String checkDate; //当前选择的回放日期
    private MediaPlayHelper playHelper;
    private List<DateData> dotList = new ArrayList<>();

    private void initVideoPlayer(SLSVideoTextureView videoView){
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);
        options.setInteger(AVOptions.KEY_MAX_CACHE_BUFFER_DURATION, 3000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 1024 * 8);
        videoView.setAVOptions(options);
        videoView.setDisplayAspectRatio(SLSVideoTextureView.ASPECT_RATIO_16_9);

        videoView.setBufferingIndicator(mLoadingView);
        videoView.setOnCompletionListener(new PlayCompletionListener());
        videoView.setOnPreparedListener(new PlayPreparedListener());
        videoView.setOnSeekCompleteListener(new PlaySeekCompletionListener());
        videoView.setOnTouchListener(new PlayTouchListener());
        videoView.setOnErrorListener(new PlayErrorListener());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        setContentView(R.layout.media_video_playback_layout);
        initToolbar();

        initLayout();
        initBaseLineTextSwitcher();
        initVideoPlayer(playView); // 初始化播放器
        initVideoIndicator();
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
        /*RequestPlaybackMarkTask task = new RequestPlaybackMarkTask();
        task.execute();*/

        token = getIntent().getStringExtra("token");
        playHelper.loadBaseData(110, new MediaPlayHelper.MediaCallback<Collection<String>>() {
            @Override
            public void onSuccess(Collection<String> manifestDtos) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                DateData markDate = null;
                Calendar calendar = Calendar.getInstance();
                for (String s : manifestDtos) {
                    try {
                        calendar.setTime(sdf.parse(s));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    markDate = new DateData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
                    dotList.add(markDate);
                }
                markDot();
            }

            @Override
            public void onFail(Throwable throwable) {
                System.out.println(throwable);
            }
        });
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
        videoIndicator = (SengledVideoIndicator)findViewById(R.id.media_video_axis_indicator);
        playView = (SLSVideoTextureView)findViewById(R.id.list_item_video_view);
        mLoadingView = findViewById(R.id.item_loading_view);
        timeLengthView =(TextView) findViewById(R.id.play_time_length);
        playOrPauseBtn = (ImageButton) findViewById(R.id.play_or_pause_btn);
        playReferLine =  findViewById(R.id.play_refer_line);
        scrollView = (ObservableScrollView) findViewById(R.id.media_video_playback_scrollview);
        textSwitcher = (TextSwitcher) findViewById(R.id.media_baseline_text);
        countInfoView = (TextView) findViewById(R.id.media_video_playback_count_info);

        CellConfig.Month2WeekPos = CellConfig.middlePosition;
        CellConfig.ifMonth = false;
        expCalendarView.shrink();
    }

    private void initVideoIndicator(){
        playHelper = new MediaPlayHelper(playView, Const.AWS_BASE_URL);
        videoIndicator.setmLoadingView(mLoadingView);
        videoIndicator.setmPlayer(playView);
        videoIndicator.setScrollView(scrollView);
        videoIndicator.setReferView(playReferLine);
        videoIndicator.setPlayHelper(playHelper);
    }

    /**
     * 标记日期是否有回放视频
     */
    private void markDot(){
        for (DateData dateData : dotList) {
            expCalendarView.markDate(dateData.setMarkStyle(new MarkStyle(MarkStyle.DOT, Color.GREEN)));
        }
    }

    private void initEvent(){
        expCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                requestBackendData(date);
            }
        });

        videoIndicator.setMotionClickListener(new SengledVideoIndicator.MotionClickListener() {
            @Override
            public void motionClick(AxisMotion axisMotion) {
                Toast.makeText(getApplication(), axisMotion.getTimePoint().toString(),Toast.LENGTH_LONG).show();
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

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            private int lastY = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;

                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                            videoIndicator.synProgress(scroller.getScrollY());
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(touchEventId, scroller), 1);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eventAction = event.getAction();
                int y = (int) event.getRawY();
                switch (eventAction) {
                    case MotionEvent.ACTION_UP:
                        handler.sendMessageDelayed(handler.obtainMessage(touchEventId, v), 5);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        scrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener(){
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                try {
                    String showText = videoIndicator.coordinateY2Time(y);
                    textSwitcher.setText(showText);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void requestBackendData(DateData date){
        MarkedDates markedDates = expCalendarView.getMarkedDates();

        Set<DateData> removeSet = new HashSet<>();
        for (DateData dateData : markedDates.getAll()) {
            if (dotList.contains(dateData)){
                dateData.setMarkStyle(MarkStyle.DOT, Color.GREEN);
            }else{
                removeSet.add(dateData);
            }
        }
        for (DateData dateData : removeSet) {
            markedDates.remove(dateData);
        }
        markedDates.remove(date);
        date.setMarkStyle(MarkStyle.BACKGROUND, Color.parseColor("#FBC19B"));
        expCalendarView.markDate(date);
        //expCalendarView.markDate(date.setMarkStyle(new MarkStyle()));

        curYM.setText(String.format("%s年%s月",date.getYear(),date.getMonth()));

        playView.stopPlayback();

        checkDate = String.format("%d-%s-%s",date.getYear(),date.getMonthString(),date.getDayString());

        //new RequestPlaybackVideoTask(getApplication()).execute(token,checkDate);
        requestAwsData(token, checkDate);
        new RequestPlaybackMotionTask(getApplication()).execute();
    }

    private void requestAwsData(String token, String checkDate){
        playHelper.loadAWSData(token, checkDate, new MediaPlayHelper.MediaCallback<List<MediaPlayerDto.PlaybackDto>>() {
            @Override
            public void onSuccess(List<MediaPlayerDto.PlaybackDto> list) {
                List<AxisVideo> axisVideos = toAxisVideo(list);

                countInfoView.setText(String.format("%d video files", axisVideos.size()));
                videoIndicator.setVideoBeanData(axisVideos);
                playbackList = axisVideos;
                videoIndicator.playVideo(null);
            }

            @Override
            public void onFail(Throwable throwable) { }
        });
    }

    private List<AxisVideo> toAxisVideo(List<MediaPlayerDto.PlaybackDto> list){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<AxisVideo> retList = new ArrayList<AxisVideo>();
        Calendar calendar = Calendar.getInstance();
        for (MediaPlayerDto.PlaybackDto playbackDto : list) {
            AxisVideo axisVideo = new AxisVideo();
            axisVideo.setVideoPath(Const.AWS_BASE_URL+playbackDto.getUri());
            try {
                Date startTime = formatter.parse(playbackDto.getTime());
                axisVideo.setStartTime(startTime);
                calendar.setTime(startTime);
                calendar.add(Calendar.SECOND, playbackDto.getDuration());
                axisVideo.setEndTime(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            retList.add(axisVideo);
        }
        return retList;
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
        scrollView.scrollTo(0,0);
        countInfoView.setText(String.format("%d video files", event.axisVideos.size()));
        videoIndicator.setVideoBeanData(event.axisVideos);
        playbackList = event.axisVideos;
        videoIndicator.playVideo(null);
    }

    private static final int UPDATE_CURRENT_TIME = 0x01;
    private Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            long duration = videoIndicator.getVideoLength();
            long curPos = videoIndicator.getCurPosition();

            if (curPos <= duration){
                String totalLength = generateTime(duration);
                String curLength = generateTime(curPos);
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
            videoIndicator.playVideo(null);
            videoIndicator.setAutoCall(false);
        }
    }

    class  PlayPreparedListener implements SLSMediaPlayer.OnPreparedListener{
        @Override
        public void onPrepared(SLSMediaPlayer slsMediaPlayer) {
            timeHandler.removeMessages(UPDATE_CURRENT_TIME);
            timeHandler.sendEmptyMessageDelayed(UPDATE_CURRENT_TIME,1000);
            playOrPauseBtn.setVisibility(View.GONE);
            videoIndicator.setAutoCall(true);
        }
    }

    private String errInfo = null;
    class PlayErrorListener implements SLSMediaPlayer.OnErrorListener{

        @Override
        public boolean onError(SLSMediaPlayer slsMediaPlayer, int errorCode) {
            switch (errorCode) {
                case SLSMediaPlayer.ERROR_CODE_INVALID_URI:
                    errInfo = "Invalid URL !";
                    break;
                case SLSMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    errInfo = "404 resource not found !";
                    break;
                case SLSMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    errInfo = "Connection refused !";
                    break;
                case SLSMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    errInfo = "Connection timeout !";
                    break;
                case SLSMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    errInfo = "Stream disconnected !";
                    break;
                case SLSMediaPlayer.ERROR_CODE_IO_ERROR:
                    errInfo = "Network IO Error !";
                    break;
                case SLSMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    errInfo = "Unauthorized Error !";
                    break;
                case SLSMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    errInfo = "Prepare timeout !";
                    break;
                case SLSMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    errInfo = "Read frame timeout !";
                    break;
                case SLSMediaPlayer.ERROR_CODE_HW_DECODE_FAILURE:
                    break;
                default:
                    errInfo = errorCode + ", unknown error !";
                    break;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VideoPlaybackActivity.this, errInfo, Toast.LENGTH_LONG).show();
                }
            });
            return true;
        }
    }

    class PlaySeekCompletionListener implements  SLSMediaPlayer.OnSeekCompleteListener{

        @Override
        public void onSeekComplete(SLSMediaPlayer slsMediaPlayer) {
            videoIndicator.setAutoCall(true);
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
