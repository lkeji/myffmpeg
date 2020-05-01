package com.ffmpeg.myffmpeg.service;


import java.util.Map;

public interface SelectStmpVideoURLI {
    /*
    * 获取视频流
    * */
    public String getStmpURL(String deviceId,String channel,String Userid);
    /*
    * 关闭视频流
    * */
    public boolean delThread(String deviceId, String Userid);
    /*
     * 视频流转换
     * */
    public  String rtspTOrtmp(String deviceId,String channel,String stream);

    /*
     * 视频流重试切换码流
     * */
    public Map chargs(String deviceId, String channel, String Userid);
}
