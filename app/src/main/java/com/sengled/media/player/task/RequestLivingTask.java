package com.sengled.media.player.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sengled.media.player.common.Const;
import com.sengled.media.player.common.HttpUtils;
import com.sengled.media.player.entity.Lives;
import com.sengled.media.player.event.LivesDataRefreshEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by admin on 2017/4/12.
 */
public class RequestLivingTask extends AsyncTask<String,Void,List<Lives>> {
    private  Context mContext;

    public RequestLivingTask(Context mContext){
        this.mContext = mContext;
    }

    @Override
    protected List<Lives> doInBackground(String... params) {
        List<Lives> liveList = new ArrayList<>();
        try {
            String livingList = HttpUtils.getInstance().get(Const.MEDIA_SERVER_URL_220);
            Log.i("Player",livingList);
            JSONArray jsonArray = new JSONArray(livingList);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject live = jsonArray.optJSONObject(i);
                Lives lives = new Lives(live,Const.MEDIA_SERVER_RTSP_ADDR_220,Const.PREVIEW_IMAGE_PREFIX_PATH, Const.PREVIEW_IMAGE_SUFFIX_PATH);

                if (Const.includeTokens.contains(lives.getToken())){
                    liveList.add(lives);
                };
            }
            Log.i("Player221",livingList);

            //=====================
            /*for (String testUrl : Const.testUrls) {
                Lives videolives = new Lives(null,testUrl, Const.PREVIEW_IMAGE_PREFIX_PATH, Const.PREVIEW_IMAGE_SUFFIX_PATH);
                liveList.add(videolives);
            }*/
            //========end=============

            String livingList221 = HttpUtils.getInstance().get(Const.MEDIA_SERVER_URL_221);
            JSONArray jsonArray221 = new JSONArray(livingList221);
            for (int j = 0; j < jsonArray221.length(); j++) {
                JSONObject live = jsonArray221.optJSONObject(j);
                Lives lives = new Lives(live,Const.MEDIA_SERVER_RTSP_ADDR_221,Const.PREVIEW_IMAGE_PREFIX_PATH, Const.PREVIEW_IMAGE_SUFFIX_PATH);
                if (Const.includeTokens.contains(lives.getToken())){
                    liveList.add(lives);
                };
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return liveList;
    }

    ProgressDialog mProgressDialog ;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mContext,"加载中……",null,false);
    }

    @Override
    protected void onPostExecute(List<Lives> lives) {
        super.onPostExecute(lives);
        EventBus.getDefault().post(new LivesDataRefreshEvent(lives));
        mProgressDialog.dismiss();
    }
}
