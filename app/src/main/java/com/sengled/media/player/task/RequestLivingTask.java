package com.sengled.media.player.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sengled.media.player.common.HttpUtils;
import com.sengled.media.player.entity.Lives;
import com.sengled.media.player.event.LivesDataRefreshEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/12.
 */
public class RequestLivingTask extends AsyncTask<String,Void,List<Lives>> {

    private static final String[] testUrls= new String[]{
            "http://192.168.1.100:9000/gongfu.mp4",
            "http://192.168.1.100:9000/merge-190454.flv",
            "rtmp://live.hkstv.hk.lxdns.com/live/hks",
            "https://jx1-amazon-storage-test.cloud.sengled.com:9000/amazon-storage/download?bucketName=sengled-test-video-cn-north-1&filename=a6b9d040-4cc5-4120-8ad7-aaaa9a3486d0.mp4",
            "https://cn-amazon-storage.cloud.sengled.com:9000/amazon-storage/download?bucketName=sengledvideobucket-cn-north-1&filename=97773d2d-89fa-4aa2-8c31-6c1549a0bf2b.mp4",
            "https://cn-amazon-storage.cloud.sengled.com:9000/amazon-storage/download?bucketName=sengledvideobucket-cn-north-1&filename=ddbf95ff-1940-4c27-82f4-23921be352f0.mp4",
            "https://cn-amazon-storage.cloud.sengled.com:9000/amazon-storage/download?bucketName=sengledvideobucket-cn-north-1&filename=299b97df-0ff1-4bda-ac26-13d26dcb6e85.mp4",
            "https://cn-amazon-storage.cloud.sengled.com:9000/amazon-storage/download?bucketName=sengledvideobucket-cn-north-1&filename=9c2d4726-f0ad-487d-bf15-e0b0e6b28a29.mp4",
            "https://cn-amazon-storage.cloud.sengled.com:9000/amazon-storage/download?bucketName=sengledvideobucket-cn-north-1&filename=0d3be30f-c2b8-4a6e-94f3-c6fd0a2d3d6d.mp4",
            "https://cn-amazon-storage.cloud.sengled.com:9000/amazon-storage/download?bucketName=sengledvideobucket-cn-north-1&filename=09d78a4c-2c52-41a7-8bdb-efc2f7f8d750.mp4"
    };

    private static final String url = "http://120.55.238.158/api/live/near_recommend?lc=3000000000011509&cv=IK3.1.10_Android&cc=TG36008&ua=XiaomiMI4LTE&uid=190761403&sid=20Tr1VWDFRc5wxUub4BC6rl55284NDi0VsCuvi2aZi2h59JJRfVDI&devi=867323029795190&imsi=460002330273772&imei=867323029795190&icc=898600520115f0989782&conn=WIFI&vv=1.0.3-2016060211417.android&aid=6524c2b6ae0bb697&osversion=android_23&mtid=4c8b78842db191e46d8639b709d1fa38&mtxid=fcd7333d06da&proto=4&smid=DujlPyXDfceh+88GbEzm+rhiRWdHAXcqw3ASJWkadmdHVmg5HpwVO2vPVauokUmZh2DI3gKxpE7rh4aEPx32U3LA&longitude=116.32758&latitude=39.75431";
    //private static final String mediaServerUrl220="http://101.68.222.220:8888/media/streams";
    private static final String mediaServerUrl220="http://10.100.102.29:8888/media/streams";
    //private static final String mediaServerUrl221="http://101.68.222.221:8888/media/streams";
    private static final String mediaServerUrl221="http://10.100.102.29:8888/media/streams";
    //private static final String mediaServerRtspAddr220="rtsps://101.68.222.220:1554";
    private static final String mediaServerRtspAddr220="rtsps://10.100.102.29:1554";
    //private static final String mediaServerRtspAddr221="rtsps://101.68.222.221:1554";
    private static final String mediaServerRtspAddr221="rtsps://10.100.102.29:1554";

    private static final String rtsp="rtsp://101.68.222.220:554/DD69960CE1DF6C27EBED2B7889CD8F5A.sdp";
    private static final String imagePrefixPath="http://jx1.amazon-storage.test.cloud.sengled.com:8000/amazon-storage/download?bucketName=sengled-test-image-cn-north-1&filename=";
    private static final String imageSuffixPath="_small.jpg";

    private  Context mContext;

    public RequestLivingTask(Context mContext){
        this.mContext = mContext;
    }

    @Override
    protected List<Lives> doInBackground(String... params) {
        List<Lives> liveList = new ArrayList<>();
        try {
            String livingList = HttpUtils.getInstance().get(mediaServerUrl220);
            Log.i("Player",livingList);
            JSONArray jsonArray = new JSONArray(livingList);
            //JSONObject livingObj = new JSONObject(livingList);
            //JSONArray livesArr = livingObj.optJSONArray("lives");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject live = jsonArray.optJSONObject(i);
                Lives lives = new Lives(live,mediaServerRtspAddr220,imagePrefixPath,imageSuffixPath);
                liveList.add(lives);
            }
            Log.i("Player221",livingList);

            //=====================
            for (String testUrl : testUrls) {
                Lives videolives = new Lives(null,testUrl,imagePrefixPath,imageSuffixPath);
                liveList.add(videolives);
            }
            //========end=============

            String livingList221 = HttpUtils.getInstance().get(mediaServerUrl221);
            JSONArray jsonArray221 = new JSONArray(livingList221);
            for (int j = 0; j < jsonArray221.length(); j++) {
                JSONObject live = jsonArray221.optJSONObject(j);
                Lives lives = new Lives(live,mediaServerRtspAddr221,imagePrefixPath,imageSuffixPath);
                liveList.add(lives);
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
