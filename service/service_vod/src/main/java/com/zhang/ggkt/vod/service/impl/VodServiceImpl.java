package com.zhang.ggkt.vod.service.impl;

import com.atguigu.ggkt.model.vod.Video;
import com.atguigu.ggkt.model.vod.VideoVisitor;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaResponse;
import com.zhang.ggkt.exception.GgktException;
import com.zhang.ggkt.vod.service.VideoService;
import com.zhang.ggkt.vod.service.VodService;
import com.zhang.ggkt.vod.utils.ConstantPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class VodServiceImpl implements VodService {

    @Autowired
    private VideoService videoService;

    @Value("${tencent.video.appid}")
    private String appId;

    //上传视频
    @Override
    public String updateVideo() {
        try {
            VodUploadClient client =
                    new VodUploadClient(ConstantPropertiesUtil.ACCESS_KEY_ID,
                            ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            VodUploadRequest request = new VodUploadRequest();
            //视频本地地址
            request.setMediaFilePath("D:\\ikun.mp4");
            //指定任务流
            request.setProcedure("LongVideoPreset");
            //调用上传方法，传入接入点地域及上传请求。
            VodUploadResponse response = client.upload("ap-shanghai", request);
            //返回文件id保存到业务表，用于控制视频播放
            String fileId = response.getFileId();
            return fileId;
        } catch (Exception e) {
            throw new GgktException(20001,"上传视频失败！");
        }
    }

    //删除腾讯云视频
    @Override
    public void removeVideo(String fileId) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            Credential cred =
                    new Credential(ConstantPropertiesUtil.ACCESS_KEY_ID,
                            ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            // 实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, "");
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DeleteMediaRequest req = new DeleteMediaRequest();
            req.setFileId(fileId);
            // 返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
            DeleteMediaResponse resp = client.DeleteMedia(req);
            // 输出json格式的字符串回包
            System.out.println(DeleteMediaResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            throw new GgktException(20001,"删除视频失败！");
        }
    }

    //获取视频id，小节id
    @Override
    public Map<String, Object> getPlayAuth(Long courseId, Long videoId) {
        //根据小节id
        Video video = videoService.getById(courseId);
        if (video == null) throw  new GgktException(20001,"小节信息不存咋在");
        Map<String,Object> map = new HashMap<>();
        map.put("videoSourceId",video.getVideoSourceId());
        System.out.println(video.getVideoSourceId()+"-----------------------------");
        map.put("appId",appId);
        return map;
    }
}
