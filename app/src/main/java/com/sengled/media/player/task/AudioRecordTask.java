package com.sengled.media.player.task;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.util.Log;

import com.bjbj.sls.talkback.SLSTalkback;

import java.io.DataInputStream;

/**
 * Created by admin on 2017/6/30.
 */
public class AudioRecordTask implements Runnable {

    private boolean isRecording = false;
    private AudioRecord audioRecord;
    private SLSTalkback talkback;
    private String url;
    private static final int audioSource = MediaRecorder.AudioSource.MIC;
    private static final int sampleRate = 8000;
    private static final int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_FRAME_SIZE =640;
    private int audioBufSize = 0;

    private short[] samples;
    private int bufferRead = 0;
    private int bufferSize = 0;

    public AudioRecordTask(SLSTalkback talkback,String url)
    {
        this.talkback = talkback;
        this.url = url;
        //initRecord();
    }

    /**
     * 实例化audioRecord对象
     */
    private void initRecord () {
        if(audioRecord != null) return;

        bufferSize = BUFFER_FRAME_SIZE;
        audioBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig,audioFormat);
        if (audioBufSize == AudioRecord.ERROR_BAD_VALUE){
            //do something
            return;
        }
        samples = new short[audioBufSize];
        audioRecord = new AudioRecord(audioSource, sampleRate,
                channelConfig, audioFormat, audioBufSize);
    }

    public int startRecording()
    {
        initRecord();
        int ret =  talkback.startTalkback(url);
        return ret;
    }

    @Override
    public void run() {
        try
        {
            audioRecord.startRecording();
        }
        catch (IllegalStateException e)
        {
            setRecording(false);
            return;
        }
        setRecording(true);
        long start = System.currentTimeMillis();
        while (isRecording())
        {
            bufferRead = audioRecord.read(samples, 0, audioBufSize);
            Log.d("ijkmedia","bufferRead = "+bufferRead +" buffersize="+bufferSize);
            if (AudioRecord.ERROR_INVALID_OPERATION != bufferRead)
            {
                talkback.sendMessage(toByteArray(samples));
            }
        }
        audioRecord.stop();
        Log.w("IJKMEDIA","stop recored");
    }

    public synchronized boolean isRecording() {
        return isRecording;
    }
    public synchronized void setRecording(boolean isRecording) {
        this.isRecording = isRecording;
    }

    public void stopRecording()
    {
        setRecording(false);
        talkback.destroyTalkback();
    }

    public double countDb()
    {

        float maxAmplitude = 0;
        for (int i = 0; i < samples.length; i++)
        {
            maxAmplitude += samples[i] * samples[i];
        }
        // 平方和除以数据总长度，得到音量大小。
        double mean = maxAmplitude / (double) bufferRead;
        double volume = 10 * Math.log10(mean);
        return volume;
    }

    public byte[] toByteArray(short[] src) {

        byte[] bytes = new byte[src.length*2];
        for (int i = 0; i< src.length ; i ++) {
            bytes[i * 2 + 0] = (byte)(0xFF & (src[i] >> 0));
            bytes[i * 2 + 1] = (byte)(0xFF & (src[i] >> 8));
        }
        return bytes;
    }
}
