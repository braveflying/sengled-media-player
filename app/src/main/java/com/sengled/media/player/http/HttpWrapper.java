package com.sengled.media.player.http;

import android.content.Context;

import com.sengled.media.player.common.Const;

import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by admin on 2017/6/28.
 */
public class HttpWrapper {

    private static HttpWrapper invoker;

    private Retrofit retrofitCamera;

    private Retrofit retrofitAWS;

    private HttpWrapper(Context context){
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new AddCookiesInterceptor(context));
        retrofitCamera = new Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl(Const.CAMERA_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitAWS = new Retrofit.Builder()
                .baseUrl(Const.AWS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    };

    public static HttpWrapper getInstance(Context context){
        if (invoker == null){
            synchronized (HttpWrapper.class){
                if (invoker == null){
                    invoker = new HttpWrapper(context);
                }
            }

        }
        return invoker;
    }

    public <T> T createCameraService(Class<T> service){
        return retrofitCamera.create(service);
    }

    public <T> T createAWSService(Class<T> service){
        return retrofitAWS.create(service);
    }
}
