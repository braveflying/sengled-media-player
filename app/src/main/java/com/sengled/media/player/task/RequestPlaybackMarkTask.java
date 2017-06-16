package com.sengled.media.player.task;

import android.os.AsyncTask;

import com.sengled.media.player.event.PlaybackEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/5/26.
 */
public class RequestPlaybackMarkTask extends AsyncTask<String, Void, List<String>> {


    @Override
    protected List<String> doInBackground(String... params) {

        List<String> dateList  = new ArrayList<>();
        dateList.add("2017-05-26");
        dateList.add("2017-05-22");
        dateList.add("2017-05-23");


        return dateList;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);

        PlaybackEvent.UpdateMarkEvent markEvent = new PlaybackEvent.UpdateMarkEvent();
        markEvent.dateList = strings;
        EventBus.getDefault().post(markEvent);
    }
}
