package com.ffmpeg.myffmpeg.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ffmpeg.myffmpeg.data.CommandTasker;
import com.ffmpeg.myffmpeg.service.CommandManagerI;
import com.ffmpeg.myffmpeg.service.RedisServiceI;
import com.ffmpeg.myffmpeg.service.SelectStmpVideoURLI;
import com.ffmpeg.myffmpeg.util.ExecUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author: lkeji
 * @create: 2020-04-30 15:25
 * @description:
 * @program: myffmpeg
 */
@Service
public class SelectStmpVideoURLImpl implements SelectStmpVideoURLI {
    private static Map<String, CommandTasker> re = new HashMap<>();
    @Autowired
    private RedisServiceI redisService;

    @Override
    public String getStmpURL(String deviceId, String channel, String Userid) {
        //默认码流编号为2，如果不能播放重试修改为1
        String stream = "2";

        if (StringUtils.isEmpty(deviceId)) {
            return null;
        }
        if (StringUtils.isEmpty(channel)) {
            return null;
        }
        if (StringUtils.isEmpty(Userid)) {
            return null;
        }
        String stmpURL = null;
        //判断redis是否存在设备ID
        if (redisService.hasKey(deviceId)) {
            Map<String, Object> redisDevices = JSONObject.parseObject(redisService.get(deviceId).toString());
            int muber = (Integer) redisDevices.get("requestNumber");
            stmpURL = redisDevices.get("stmpURL").toString();
            if (muber!=0) {
                //todo 可优化为修改或者覆盖，
                redisService.remo(deviceId);
                redisDevices.put("requestNumber", muber+1);
                String json = new JSONObject().toJSONString(redisDevices);
                redisService.set(deviceId, json);
            }
            return stmpURL;
        } else {
            Map<String, Object> resu = new HashMap<>();
            //流的请求数量
            int requestNumber = 1;
            //开始视频流转换
            stmpURL = rtspTOrtmp(deviceId, channel, stream);
            resu.put("requestNumber", requestNumber++);
            resu.put("stmpURL", stmpURL);
            String json = new JSONObject().toJSONString(resu);
            redisService.set(deviceId, json);
            return stmpURL;
        }
    }

    @Override
    public boolean delThread(String deviceId, String Userid) {
        boolean stop = false;
        Map<String, Object> redisDevices = JSONObject.parseObject(redisService.get(deviceId).toString());
        int muber = (Integer) redisDevices.get("requestNumber");
        if (muber == 0) {
            stop = delThread(deviceId);
        }else {
            if ( muber-1==0){//如果减完等于0，直接销毁
                stop = delThread(deviceId);
            }else {
                redisDevices.put("requestNumber", muber-1);
                String json = new JSONObject().toJSONString(redisDevices);
                redisService.set(deviceId, json);
                stop =true;
            }
        }
        return stop;
    }
    //该视频设备没有用户获取该流
    // 销毁步骤:
    //  步骤1删除redis,
    //  步骤2删除全局的map
    private boolean delThread(String deviceId){
        boolean temp=false;
        redisService.remo(deviceId);//删除redis的userid
        if (re.containsKey(deviceId)) {
            CommandTasker commandTasker = re.get(deviceId);
            ExecUtil.stop(commandTasker);//销毁线程
            Collection<String> col = re.keySet();
            while (true == col.contains(deviceId)) {
                temp=col.remove(deviceId);
            }
        }
        return temp;
    }



    @Override
    public String rtspTOrtmp(String deviceId, String channel, String stream) {
        CommandManagerI manager = new CommandManagerIImpl();
        String result = null;
        // -rtsp_transport tcp
        //测试多个任何同时执行和停止情况
        //false表示使用配置文件中的ffmpeg路径，true表示本条命令已经包含ffmpeg所在的完整路径
        result = "rtmp://localhost:1935/live/" + deviceId;
        CommandTasker starts = manager.starts(deviceId, "ffmpeg -rtsp_transport tcp -i rtsp://172.16.0.4:9020/device/" + deviceId + "/channel/" + channel + "/stream/" + stream + " -vcodec copy -acodec copy -f flv -y " + result, false);
        re.put(starts.getId(), starts);
        return result;
    }

    @Override
    public Map chargs(String deviceId, String channel, String Userid) {
        boolean stop = false;
        String results=null;
        Map<String,Object>result=new HashMap<>();
        if (!StringUtils.isEmpty(deviceId)) {
                Collection<String> col = re.keySet();
                while (true == col.contains(deviceId)) {
                    String stream = re.get(deviceId).getCommand().split("stream")[1].substring(1, 2);
                    if (stream.equals("2")){
                        CommandTasker commandTasker = re.get(deviceId);
                        ExecUtil.stop(commandTasker);//销毁线程
                        stop = col.remove(deviceId);
                        result.put("stop",stop);
                        break;
                    }else{
                        result.put("stop",false);
                        result.put("results","视频设备ID："+deviceId+"流已重试，无法播放请联系管理员");
                        break;
                    }
                }
                //todo 判断map和redis都存在，redis不动，删除MAP的值，停止该设备的线程，然后修改码流再重新获取视频流
                if (stop && redisService.hasKey(deviceId)) {
                    results = rtspTOrtmp(deviceId, channel, "1");
                    result.put("results",results);
                }

        }
        return result;
    }
}
