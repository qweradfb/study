package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.config.RabbitMQConfig;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MediaProcessTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaProcessTask.class);

    //ffmpeg绝对路径
    @Value("${xc-service-manage-media.ffmpeg-path}")
    private String ffmpeg_path;
    //上传文件根目录
    @Value("${xc-service-manage-media.video-location}")
    private String serverPath;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory="customContainerFactory")
    public void receiveMediaProcessTask(String msg) throws IOException {
        LOGGER.info("receive media msg :"+msg);
        Map map = JSON.parseObject(msg, Map.class);
        String mediaId = (String) map.get("mediaId");
        if (mediaId==null) return;
        MediaFile mediaFile = null;
        Optional<MediaFile> byId = mediaFileRepository.findById(mediaId);
        if (byId.isPresent()){
            mediaFile = byId.get();
        }else {
            LOGGER.error("no file with named "+mediaFile);
            return;
        }
        String fileType = mediaFile.getFileType();
        String filePath = mediaFile.getFilePath();
        String fileName = mediaFile.getFileName();
        String fileId = mediaFile.getFileId();
        if ("avi".equals(fileType)){
            mediaFile.setProcessStatus("303001");
            mediaFileRepository.save(mediaFile);
        }else{
            //不需要处理
            mediaFile.setProcessStatus("303004");
            mediaFileRepository.save(mediaFile);
            return;
        }
        //将avi转为MP4
//        String ffmpeg_path, String video_path, String mp4_name, String mp4folder_path
        String video_path = serverPath+filePath+fileName;
        String mp4_name = fileId+".mp4";
        String mp4folder_path = serverPath+filePath;
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_path);
        String result = mp4VideoUtil.generateMp4();
        if (result==null||!"success".equals(result)){
            mediaFile.setProcessStatus("303003");
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            LOGGER.error("convert to mp4 format fail:"+result);
            return ;
        }
        //生成mcu8
//        String ffmpeg_path, String video_path, String m3u8_name,String m3u8folder_path
        String mp4Path = serverPath+filePath+mp4_name;
        String m3u8_name = fileId+".m3u8";
        String m3u8folder_path = serverPath+filePath+"hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path,mp4Path,m3u8_name,m3u8folder_path);
        String hlsResult = hlsVideoUtil.generateM3u8();
        if (hlsResult==null||!"success".equals(hlsResult)){
            mediaFile.setProcessStatus("303003");
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            mediaFileProcess_m3u8.setErrormsg(hlsResult);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            mediaFileRepository.save(mediaFile);
            LOGGER.error("convert to mp4 format fail:"+hlsResult);
            return ;
        }
        List<String> ts_list = hlsVideoUtil.get_ts_list();
        MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
        mediaFileProcess_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
        mediaFile.setFileUrl(filePath+"hls/"+m3u8_name);
        mediaFile.setProcessStatus("303002");
        mediaFileRepository.save(mediaFile);

    }



    
}
