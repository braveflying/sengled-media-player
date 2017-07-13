package com.sengled.media.player.task;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sengled.media.player.common.Const;
import com.sengled.media.player.entity.PlaybackDto;
import com.sengled.media.player.event.PlaybackEvent;
import com.sengled.media.player.http.HttpAWSInvoker;
import com.sengled.media.player.http.HttpAWSService;
import com.sengled.media.player.widget.timeaxis.AxisVideo;
import com.sengled.media.player.widget.timeaxis.AxisVideoBean;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by admin on 2017/5/26.
 */
public class RequestPlaybackVideoTask extends AsyncTask<String, Void, List<AxisVideo>> {

    private Context mContext;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public RequestPlaybackVideoTask(Context mContext){
        this.mContext = mContext;
    }


    @Override
    protected List<AxisVideo> doInBackground(final String... params) {
        String token = params[0];
        final String date = params[1];
        Call<List<PlaybackDto>> caller = HttpAWSInvoker.getInvoker(mContext).fetchPlaybackList(token, date);
        caller.enqueue(new Callback<List<PlaybackDto>>() {
            @Override
            public void onResponse(Call<List<PlaybackDto>> call, Response<List<PlaybackDto>> response) {
                Calendar calendar = Calendar.getInstance();
                List<AxisVideo> retList = new ArrayList<AxisVideo>();
                AxisVideo axisVideo = null;
                for (PlaybackDto playbackDto : response.body()) {
                    axisVideo = new AxisVideo();
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
                EventBus.getDefault().post(new PlaybackEvent.UpdateAxisVideoEvent(retList));
            }

            @Override
            public void onFailure(Call<List<PlaybackDto>> call, Throwable t) {
                System.out.println(t);
            }
        });
        return null;
    };

    @Override
    protected void onPostExecute(List<AxisVideo> axisVideoBeen) {
        /*String result = getResultText2();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        List<AxisVideo> retList = gson.fromJson(result, new TypeToken<List<AxisVideo>>() {}.getType());
        EventBus.getDefault().post(new PlaybackEvent.UpdateAxisVideoEvent(retList));*/
    }

    private String getResultText2(){

        InputStream is= null;
        try {
            is = mContext.getAssets().open("static/playback_video.json");
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            byte[] bytes=new byte[1024];
            int length;
            while((length=is.read(bytes))!=-1){
                baos.write(bytes,0,length);
            }
            return new String(baos.toByteArray(),"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    };


    private String getResultText (){
        InputStream fileInput = null;
        StringBuffer buffer = new StringBuffer();
        try {
            fileInput = mContext.getAssets().open("static/playback_video.json");

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
