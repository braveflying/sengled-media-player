package com.sengled.media.player.http;

import android.content.Context;

import com.sengled.media.player.entity.PlaybackDto;
import com.sengled.media.player.event.PlaybackEvent;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by admin on 2017/6/30.
 */
public class HttpAWSInvoker {

    private static HttpAWSInvoker invoker;
    private HttpWrapper httpWrapper;
    private HttpAWSService service;

    private HttpAWSInvoker(Context context){
        httpWrapper = HttpWrapper.getInstance(context);
        service = httpWrapper.createAWSService(HttpAWSService.class);
    }

    public static HttpAWSInvoker getInvoker(Context context){
        if (invoker == null){
            invoker = new HttpAWSInvoker(context);
        }
        return invoker;
    }

    public Call<List<PlaybackDto>> fetchPlaybackList(String token, String date){
        return service.fetchPlaybackList(token, date);
    }
}
