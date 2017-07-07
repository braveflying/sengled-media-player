package com.sengled.media.player.http;

import android.content.Context;
import android.content.SharedPreferences;

import com.sengled.media.player.common.Const;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by admin on 2017/6/28.
 */
public class AddCookiesInterceptor implements Interceptor {

    private Context mContext;

    public AddCookiesInterceptor(Context mContext) {
        super();
        this.mContext = mContext;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request.Builder builder = chain.request().newBuilder();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(Const.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String sessionId = sharedPreferences.getString(Const.SESSION_ID, "");
        builder.addHeader("Cookie", "JSESSIONID=" + sessionId);
        return chain.proceed(builder.build());
    }
}
