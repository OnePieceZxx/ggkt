package com.zhang.ggkt.vod.service;

import java.util.Map;

public interface VodService {
    String updateVideo();

    void removeVideo(String fileId);

    Map<String,Object> getPlayAuth(Long courseId, Long videoId);
}
