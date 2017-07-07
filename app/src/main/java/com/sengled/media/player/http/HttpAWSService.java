package com.sengled.media.player.http;

import com.sengled.media.player.entity.PlaybackDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by admin on 2017/6/28.
 */
public interface HttpAWSService {

    @Headers({"Accept: application/json"})
    @GET("/amazon-storage/playback/{token}.list")
    Call<List<PlaybackDto>> fetchPlaybackList(@Path("token") String token, @Query("t") String date);
}
