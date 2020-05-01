package com.ffmpeg.myffmpeg.controller;


import com.ffmpeg.myffmpeg.common.CommonResult;
import com.ffmpeg.myffmpeg.service.SelectStmpVideoURLI;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @program: mymall
 * @description: stmp视频流
 * @author: lkeji
 * @create: 2020-04-16 09:34
 **/
@RestController
@Api(tags={"stmp视频流"})
@RequestMapping("/selectStmpVideoURLController")
public class SelectStmpVideoURLController {
    @Autowired
    private SelectStmpVideoURLI selectStmpVideoURLI;

    @ApiOperation("获取stmp视频流")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId",value = "设备编号",paramType = "path",dataType = "string"),
            @ApiImplicitParam(name = "channel",value = "通道号",paramType = "path",dataType = "string"),
            @ApiImplicitParam(name = "Userid",value = "用户ID",paramType = "path",dataType = "string")
    })
    @RequestMapping(value = "/getStmpURL", method = RequestMethod.POST)
    public CommonResult getStmpURL(String deviceId,String channel, String Userid) {
        CommonResult commonResult;
        String count = selectStmpVideoURLI.getStmpURL(deviceId,channel,Userid);
        commonResult = CommonResult.success(count);
        return commonResult;
    }
    @ApiOperation("关闭stmp视频流")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId",value = "设备编号",paramType = "path",dataType = "string"),
            @ApiImplicitParam(name = "Userid",value = "用户ID",paramType = "path",dataType = "string")
    })
    @RequestMapping(value = "/shutDownStmp", method = RequestMethod.POST)
    public CommonResult shutDownStmp(String deviceId, String Userid) {
        CommonResult commonResult;
        boolean result = selectStmpVideoURLI.delThread(deviceId, Userid);
        if (!result){
            commonResult = CommonResult.failed("关闭stmp视频流失败");
        }else {
            return CommonResult.success("关闭stmp视频流成功");
        }
        return commonResult;
    }
    @ApiOperation("重试stmp视频流")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId",value = "设备编号",paramType = "path",dataType = "string"),
            @ApiImplicitParam(name = "channel",value = "通道号",paramType = "path",dataType = "string"),
            @ApiImplicitParam(name = "Userid",value = "用户ID",paramType = "path",dataType = "string")
    })
    @RequestMapping(value = "/editStmpURL", method = RequestMethod.POST)
    public CommonResult EditStmpURL(String deviceId,String channel, String Userid) {
        CommonResult commonResult;
        Map chargs = selectStmpVideoURLI.chargs(deviceId, channel, Userid);
        commonResult = CommonResult.success(chargs);
        return commonResult;
    }

}
