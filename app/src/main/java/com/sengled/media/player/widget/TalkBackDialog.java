package com.sengled.media.player.widget;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjbj.sls.talkback.MediaTalkbackHelper;
import com.bjbj.sls.talkback.SLSTalkback;
import com.sengled.media.player.R;
import com.sengled.media.player.activity.LoginActivity;
import com.sengled.media.player.common.ConfigUtils;
import com.sengled.media.player.common.Const;
import com.sengled.media.player.http.HttpAWSInvoker;
import com.sengled.media.player.http.HttpCameraInvoker;
import com.sengled.media.player.task.AudioRecordTask;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 2017/7/14.
 */
public class TalkBackDialog extends DialogFragment{

    private MediaTalkbackHelper talkbackHelper;
    private ImageView imageView;
    private TextView textView;
    private long startRecordTime;

    private AudioRecordTask recordTask;
    private SLSTalkback mtalkback;
    private String token;

    private static final int UPDATE_FLAG = 0x1000;
    private Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            /*if (recordTask.isRecording()){
                setLevel((int)recordTask.countDb());
                setTime(System.currentTimeMillis() - startRecordTime);
            }
            this.sendEmptyMessageDelayed(UPDATE_FLAG, 300);*/
            if (talkbackHelper.isRecording()){
                setLevel((int)talkbackHelper.countDb());
                setTime(System.currentTimeMillis() - startRecordTime);
            }
            this.sendEmptyMessageDelayed(UPDATE_FLAG, 300);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = getArguments().getString("token");
        talkbackHelper = new MediaTalkbackHelper(Const.AWS_BASE_URL,token,"1");
        talkbackHelper.startTalkback(new MediaTalkbackHelper.TalkbackCallback<String>(){
            @Override
            public void onSuccess(String s) {
                startRecordTime = System.currentTimeMillis();
                updateHandler.removeMessages(UPDATE_FLAG);
                updateHandler.sendEmptyMessage(UPDATE_FLAG);
            }

            @Override
            public void onFail(Throwable throwable) {

            }
        });

        /*Call<Map<String, String>> caller = HttpAWSInvoker.getInvoker(getActivity()).fetchTalkbackUrl(token, "1");
        caller.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                Map<String,String> body = response.body();
                mtalkback = new SLSTalkback();
                recordTask = new AudioRecordTask(mtalkback, body.get("rtsp"));
                startRecorder();
                startRecordTime = System.currentTimeMillis();
                updateHandler.removeMessages(UPDATE_FLAG);
                updateHandler.sendEmptyMessage(UPDATE_FLAG);
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {

            }
        });*/

    }

    @Override
    public void onStart() {
        super.onStart();
        Window mWindow = getDialog().getWindow();
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#32000000")));
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.dimAmount =0f;
        //lp.dimAmount = 0.90f;
        lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        mWindow.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.media_talkback_dialog_layout, container, false);
        imageView = (ImageView) view.findViewById(android.R.id.progress);
        textView = (TextView) view.findViewById(android.R.id.text1);
        return view;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //endTalkback();
        talkbackHelper.stopRecording();
    }

    public void setLevel(int level) {
        System.out.println("record db == " + level);
        Drawable drawable = imageView.getDrawable();
        drawable.setLevel(2000+ 7000 * level / 100);
    }

    public void setTime(long time) {
        long seconds = (time/1000) % 60;
        long minutes = (time/1000 / 60) % 60;
        textView.setText(String.format("%02d:%02d",minutes, seconds));
    }

    private void startRecorder(){
        String sessionId = ConfigUtils.getInstance(this.getActivity()).getConfig(Const.SESSION_ID);
        if (sessionId == null || sessionId.isEmpty()){
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }else {
            startTalkback();
        }
    }

    /*private int deviceid1 = 10637;
    private String uuid1 = "B0:CE:18:FF:02:6C";
    private String url="rtsp://101.68.222.221:2554/7947B6B48864E301AC3064E426F33403_3C8D8A1C-0D0D-4F2F-9995-787D9FF06C47.sdp";*/
    private void startTalkback(){

        if (recordTask.startRecording()<0){
            // 开始录制失败
            return;
        }
        new Thread(recordTask).start();
        /*Map<String,Object> talkbackMapStart = new HashMap<String,Object>();
        talkbackMapStart.put("deviceId",deviceid1);
        talkbackMapStart.put( "uuid",uuid1);
        talkbackMapStart.put("operateType",1);
        talkbackMapStart.put("operateTime","2017-06-14 16:36:00");
        talkbackMapStart.put( "voiceTalkUrl",url);

        Call<Map<String,Object>> caller = HttpCameraInvoker.getInvoker(this.getActivity()).startStopVoiceTransfer(talkbackMapStart);
        caller.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                System.out.println(response.body());
                if (recordTask.startRecording()<0){
                    // 开始录制失败
                    return;
                }
                new Thread(recordTask).start();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });*/
    }

    private void endTalkback(){
        recordTask.stopRecording();
        /*Map<String,Object> talkbackMapEnd = new HashMap<String,Object>();
        talkbackMapEnd.put("operateType",2);
        talkbackMapEnd.put("deviceId",deviceid1);
        talkbackMapEnd.put("uuid",uuid1);
        talkbackMapEnd.put("operateTime","2017-06-14 16:36:00");
        talkbackMapEnd.put("voiceTalkUrl",url);

        Call<Map<String,Object>> caller = HttpCameraInvoker.getInvoker(this.getActivity()).startStopVoiceTransfer(talkbackMapEnd);
        caller.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                System.out.println(response.body());
                recordTask.stopRecording();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {

            }
        });*/
    }
}
