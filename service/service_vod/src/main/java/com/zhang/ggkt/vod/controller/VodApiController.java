package com.zhang.ggkt.vod.controller;

import com.zhang.ggkt.result.Result;
import com.zhang.ggkt.vod.service.VodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "腾讯视频点播")
@RestController
@RequestMapping("/api/vod")
public class VodApiController {

    @Autowired
    private VodService vodService;

    @GetMapping("getPlayAuth/{courseId}/{videoId}")
    public Result getPlayAuth(
            @ApiParam(value = "课程id", required = true)
            @PathVariable Long courseId,
            @ApiParam(value = "视频id", required = true)
            @PathVariable Long videoId) {
        return  Result.ok(vodService.getPlayAuth(courseId, videoId));
    }
}
