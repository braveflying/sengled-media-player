package com.sengled.media.player.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bjbj.sls.talkback.SLSTalkback;
import com.bjbj.slsijk.player.AVOptions;
import com.bjbj.slsijk.player.ISLSMediaController;
import com.bjbj.slsijk.player.SLSMediaPlayer;
import com.bjbj.slsijk.player.widget.SLSVideoTextureView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.typeface.IIcon;
import com.sengled.media.player.R;
import com.sengled.media.player.common.ConfigUtils;
import com.sengled.media.player.common.Const;
import com.sengled.media.player.http.HttpCameraInvoker;
import com.sengled.media.player.task.AudioRecordTask;
import com.skyfishjy.library.RippleBackground;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 2017/6/29.
 */
public class TalkbackActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(IconicsContextWrapper.wrap(base));
    }

    private RippleBackground rippleBackground;
    private ImageView micImageView;

    private SLSVideoTextureView player;
    private ISLSMediaController mediaController;
    private View loadView;
    private View coverView;

    private SLSTalkback mtalkback;
    private AudioRecordTask recordTask;
    private boolean isPress = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_talkback_layout);
        initToolbar();

        initLayout();
        initEvent();
        initTalkback();
        initVideoPlayer();
        startPlaying();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isPress){
            endTalkback();
        }
        player.stopPlayback();
    }

    private void initLayout(){
        rippleBackground=(RippleBackground)findViewById(R.id.media_ripple_wave);
        micImageView=(ImageView)findViewById(R.id.media_mic_image);
        player =(SLSVideoTextureView) findViewById(R.id.list_item_video_view);
        loadView = findViewById(R.id.item_loading_view);
        coverView = findViewById(R.id.media_video_item_cover_include_id);
        mediaController = (ISLSMediaController)findViewById(R.id.item_media_controller);
    }

    private void initVideoPlayer(){
        AVOptions options = new AVOptions();
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_PROBESIZE, 128 * 1024);
        options.setInteger(AVOptions.KEY_MEDIACODEC, AVOptions.MEDIA_CODEC_SW_DECODE);
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);
        player.setAVOptions(options);
        player.setDisplayAspectRatio(SLSVideoTextureView.ASPECT_RATIO_16_9);

        player.setBufferingIndicator(loadView);
        player.setMediaController(mediaController);
        player.setOnPreparedListener(new SLSMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(SLSMediaPlayer slsMediaPlayer) {
                coverView.setVisibility(View.GONE);
            }
        });
    }

    private void startPlaying(){
        String videoPath = getIntent().getCharSequenceExtra("videoPath").toString();
        player.setVideoPath(videoPath);
        player.start();
    }

    private void initEvent(){
        micImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    if (!isPress) {
                        isPress = true;
                        rippleBackground.startRippleAnimation();
                        setMicBackground(CommunityMaterial.Icon.cmd_microphone);

                        startRecorder();
                    }else {
                        isPress=false;
                        rippleBackground.stopRippleAnimation();
                        setMicBackground(CommunityMaterial.Icon.cmd_microphone_outline);
                        endTalkback();
                    }
                }
                return true;
            }
        });
    }

    private void setMicBackground(IIcon icon){
        Drawable drawable = new IconicsDrawable(this)
                .icon(icon)
                .color(ContextCompat.getColor(this, R.color.colorPrimary))
                .sizeDp(18);
        micImageView.setBackground(drawable);
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.media_drawer_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("视频对讲");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initTalkback(){
        mtalkback = new SLSTalkback();
        recordTask = new AudioRecordTask(mtalkback, url);
    }



    private void startRecorder(){
        String sessionId = ConfigUtils.getInstance(this).getConfig(Const.SESSION_ID);
        if (sessionId == null || sessionId.isEmpty()){
            login();
        }
        startTalkback();
    }

    private void login(){
        Map<String, Object> loginMap = new HashMap<String,Object>();
        loginMap.put("uuid","3fb5e2c7142de21c");
        loginMap.put("os_type" , "ios");
        loginMap.put("user" , "las@sengled.com");
        loginMap.put("pwd" ,123456);
        Call<Object> caller = HttpCameraInvoker.getInvoker(this).doLogin(loginMap);
        caller.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Map<String, String> body = (Map<String, String>) response.body();
                String sessionId = body.get("jsessionid");
                ConfigUtils.getInstance(TalkbackActivity.this).saveConfig(Const.SESSION_ID,sessionId);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private int deviceid1 = 10637;
    private String uuid1 = "B0:CE:18:FF:02:6C";
    private String url="rtsp://101.68.222.221:2554/7947B6B48864E301AC3064E426F33403_3C8D8A1C-0D0D-4F2F-9995-787D9FF06C47.sdp";
    private void startTalkback(){
        Map<String,Object> talkbackMapStart = new HashMap<String,Object>();
        talkbackMapStart.put("deviceId",deviceid1);
        talkbackMapStart.put( "uuid",uuid1);
        talkbackMapStart.put("operateType",1);
        talkbackMapStart.put("operateTime","2017-06-14 16:36:00");
        talkbackMapStart.put( "voiceTalkUrl",url);

        Call<Map<String,Object>> caller = HttpCameraInvoker.getInvoker(this).startStopVoiceTransfer(talkbackMapStart);
        caller.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                System.out.println(response.body());
                if (recordTask.startRecording()<0){
                    // 开始录制失败
                    return;
                };
                new Thread(recordTask).start();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });
    }

    private void endTalkback(){
        Map<String,Object> talkbackMapEnd = new HashMap<String,Object>();
        talkbackMapEnd.put("operateType",2);
        talkbackMapEnd.put("deviceId",deviceid1);
        talkbackMapEnd.put( "uuid",uuid1);
        talkbackMapEnd.put("operateTime","2017-06-14 16:36:00");
        talkbackMapEnd.put( "voiceTalkUrl",url);

        Call<Map<String,Object>> caller = HttpCameraInvoker.getInvoker(this).startStopVoiceTransfer(talkbackMapEnd);
        caller.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                System.out.println(response.body());
                recordTask.stopRecording();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });
    }
}
