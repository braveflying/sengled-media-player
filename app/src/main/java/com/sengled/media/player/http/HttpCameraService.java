package com.sengled.media.player.http;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by admin on 2017/6/28.
 */
public interface HttpCameraService {

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("led/setLedOnoff.json")
    Call<String> ledOnOff(@Body Map<String, Object> params);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("v1/customer/login.json")
    Call<Object> login(@Body Map<String, Object> params);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("ipc/startStopVoiceTransfer.json")
    Call<Map<String,Object>> startStopVoiceTransfer(@Body Map<String, Object> params);
}
