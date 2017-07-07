package com.sengled.media.player.task;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sengled.media.player.event.PlaybackEvent;
import com.sengled.media.player.widget.timeaxis.AxisMotion;
import com.sengled.media.player.widget.timeaxis.AxisMotionBean;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2017/5/26.
 */
public class RequestPlaybackMotionTask extends AsyncTask<String, Void, List<AxisMotion>> {

    private Context mContext;

    public RequestPlaybackMotionTask(Context mContext){
        this.mContext = mContext;
    }

    @Override
    protected List<AxisMotion> doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(List<AxisMotion> axisMotions) {
        String result = getResultText();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        List<AxisMotion> motionList = new ArrayList<>();//gson.fromJson(result, new TypeToken<List<AxisMotion>>(){}.getType());


        EventBus.getDefault().post(new PlaybackEvent.UpdateAxisMotionEvent(motionList));
    }


    private String getResultText (){
        InputStream fileInput = null;
        StringBuffer buffer = new StringBuffer();
        try {
            fileInput = mContext.getAssets().open("static/playback_motion.json");

            InputStreamReader reader = new InputStreamReader(fileInput, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(reader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
                buffer.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}
