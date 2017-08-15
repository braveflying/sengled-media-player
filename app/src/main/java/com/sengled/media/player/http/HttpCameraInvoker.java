package com.sengled.media.player.http;

import android.content.Context;

import com.sengled.media.player.entity.UserInfoDto;

import java.util.Map;

import retrofit2.Call;

/**
 * Created by admin on 2017/6/30.
 */
public class HttpCameraInvoker {

    private static HttpCameraInvoker invoker;
    private HttpWrapper httpWrapper;
    private HttpCameraService service;

    private HttpCameraInvoker(Context context){
        httpWrapper = HttpWrapper.getInstance(context);
        service = httpWrapper.createCameraService(HttpCameraService.class);
    }

    public static HttpCameraInvoker getInvoker(Context context){
        if (invoker == null){
            invoker = new HttpCameraInvoker(context);
        }
        return invoker;
    }

    public Call<UserInfoDto> doLogin(Map<String, Object> params){
        return service.login(params);
    }

    public Call<Map<String,Object>> startStopVoiceTransfer(Map<String, Object> params){
        return service.startStopVoiceTransfer(params);
    }
}
