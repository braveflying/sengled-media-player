package com.sengled.media.player.common;

import java.util.Arrays;
import java.util.List;

/**
 * Created by admin on 2017/6/28.
 */
public interface Const {

    static final String SHARED_PREFERENCES_NAME="snap_config";

    static final String SESSION_ID ="JSESSIONID";

    static final String CAMERA_BASE_URL = "http://jx1.snap.test.cloud.sengled.com:8000/camera/";

    //Debug环境
/*    static final String AWS_BASE_URL = "http://10.100.102.21:8080";
    static final String MEDIA_SERVER_URL_220 = "http://10.100.102.29:8888/media/streams";
    static final String MEDIA_SERVER_RTSP_ADDR_220="rtsps://10.100.102.29:1554";
    static final String PREVIEW_IMAGE_PREFIX_PATH= Const.AWS_BASE_URL+"/amazon-storage/screenshot/";
    static final String PREVIEW_IMAGE_SUFFIX_PATH="_big.jpg?size=640*360";*/

    // 嘉兴测试环境
    static final String AWS_BASE_URL = "https://jx1-amazon-storage-test.cloud.sengled.com";
    static final String MEDIA_SERVER_URL_220 = "http://101.68.222.220:8888/media/streams";
    static final String MEDIA_SERVER_URL_221 = "http://101.68.222.221:8888/media/streams";
    static final String MEDIA_SERVER_RTSP_ADDR_220="rtsps://101.68.222.220:1554";
    static final String MEDIA_SERVER_RTSP_ADDR_221="rtsps://101.68.222.221:1554";
    static final String PREVIEW_IMAGE_PREFIX_PATH= "http://jx1.amazon-storage.test.cloud.sengled.com:8000/amazon-storage/download?bucketName=sengled-test-image-cn-north-1&filename=";
    static final String PREVIEW_IMAGE_SUFFIX_PATH="_small.jpg";

    static final List<String> includeTokens = Arrays.asList(
            "2C388789E39BFEFF86B5F63511E05A45",
            "EAFA73C46E69A76FD88C2BC0E2B5C3DF",
            "7947B6B48864E301AC3064E426F33403",
            "6354CB5A95C1109D6814E94612114DD9",
            "D438197D40E7B7E121B1306D98A75FFC",
            "19E575E96E7D2D97125C5D21AE2EAFA5",
            "3146CAEAA2D074AAAE8589CE54E71600",
            "FA9A15CD885734FFDC981B189D50F602",
            "0531D614CD83AC57A941E329EDC6B0CF",
            "BF210FDE64913A765ABF6B14203D6AF0",
            "4A185F0FB6BA434FC44FF28990790CFA",
            "6D946A2D34286D439A6431970D68F70B",
            "9659B11F8C7624D4D48113641965CCFD",
            "F504F36736A574836EA7F8314E45DEEC",
            "DD69960CE1DF6C27EBED2B7889CD8F5A"
    );

    static final String[] testUrls= new String[]{
            /*"http://192.168.1.100:9000/gongfu.mp4",
            "http://192.168.1.100:9000/merge-190454.flv",
            "rtmp://live.hkstv.hk.lxdns.com/live/hks"*/
    };
}
