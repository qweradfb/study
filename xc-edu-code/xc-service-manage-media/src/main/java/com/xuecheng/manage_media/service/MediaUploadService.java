package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExecptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import jdk.nashorn.internal.ir.ReturnNode;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.io.*;
import java.util.*;

@Service
public class MediaUploadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaUploadService.class);

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.upload-location}")
    private String upload_location;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    private String routingkey_media_video;

    //获取文件的跟路径
    private String getUplaodFileFolder(String fileMd5){
        return upload_location+"/"+fileMd5.charAt(0)+"/"+fileMd5.charAt(1)+"/"+fileMd5+"/";
    }
    //得到文件目录相对路径，路径中去掉根目录
    private String getFileFolderRelativePath(String fileMd5,String fileExt){
        String filePath = fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" +
                fileMd5 + "/";
        return filePath;
    }
    //创建文件目录
    private boolean createFolder(String fileMd5){
        String path = getUplaodFileFolder(fileMd5);
        File file = new File(path);
        if (!file.exists()){
            boolean mkdirs = file.mkdirs();
            return mkdirs;
        }
        return true;
    }
    //获取分块路径
    private String getChunksPath(String fileMd5){
        return getUplaodFileFolder(fileMd5)+"chunks/";
    }
    private String getFilePath(String fileMd5,String fileExt){
        String filePath = upload_location+fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) +
                "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
        return filePath;
    }



    /**
     *
     * @param fileMd5 文件的md5值
     * @param fileName 上传文件的名称+扩展名
     * @param fileSize 上传文件的大小
     * @param mimetype
     * @param fileExt
     * @return
     */
    //文件上传注册
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //判断文件是否存在
        String fileFolder = getUplaodFileFolder(fileMd5);
        String filePath = getFilePath(fileMd5,fileExt);
        File file = new File(filePath);
        //判断数据库中是否有数据
        Optional<MediaFile> byId = mediaFileRepository.findById(fileMd5);
        if (file.exists()||byId.isPresent()) ExecptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        boolean folder = this.createFolder(fileMd5);
        //创建文件目录失败
        if (!folder) ExecptionCast.cast(MediaCode.UPLOAD_FILE_REGISTER_FAIL);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    /**
     *
     * @param fileMd5
     * @param chunk
     * @param chunkSize
     * @return
     */
    //分块检查
    public CheckChunkResult checkchunk(String fileMd5, Integer chunk, Integer chunkSize) {
        String path = getChunksPath(fileMd5);
        File file = new File(path+chunk);
        if (file.exists()){
            return new CheckChunkResult(MediaCode.CHUNK_FILE_EXIST_CHECK,true);
        }else {
            return new CheckChunkResult(MediaCode.CHUNK_FILE_NOTEXIST_CHECK,false);
        }
    }

    /**
     *
     * @param file
     * @param chunk
     * @param fileMd5
     * @return
     */
    //上传分块
    public ResponseResult uploadchunk(MultipartFile file, Integer chunk, String fileMd5) {
        if (file==null) ExecptionCast.cast(MediaCode.UPLOAD_FILE_NOTEXIST);
        String path = getChunksPath(fileMd5);
        //如果文件不存在就创建
        File chunkFile = new File(path);
        if (!chunkFile.exists()) {
            chunkFile.mkdirs();
        }
        InputStream is = null;
        FileOutputStream os = null;
        try {
            is = file.getInputStream();
            os = new FileOutputStream(path+chunk);
            IOUtils.copy(is,os);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //合并分块
    public ResponseResult mergechunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {

            //        1）将块文件合并
            String root = getUplaodFileFolder(fileMd5);
            File file = new File(root+"chunks");
            File[] files = file.listFiles();
            File initFile = new File(root+fileMd5+"."+fileExt);
            if (initFile.exists()){
                initFile.delete();
            }
        try {
            initFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File endFile = this.mergeFile(files, initFile);
//        2）校验文件md5是否正确
            boolean checkFileMd5 = checkFileMd5(endFile, fileMd5);
            if (!checkFileMd5) ExecptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
//        3）向Mongodb写入文件信息
            //将文件信息保存到数据库
            MediaFile mediaFile = new MediaFile();
            mediaFile.setFileId(fileMd5);
            mediaFile.setFileName(fileMd5+"."+fileExt);
            mediaFile.setFileOriginalName(fileName);
            //文件路径保存相对路径
            mediaFile.setFilePath(getFileFolderRelativePath(fileMd5,fileExt));
            mediaFile.setFileSize(fileSize);
            mediaFile.setUploadTime(new Date());
            mediaFile.setMimeType(mimetype);
            mediaFile.setFileType(fileExt);
            //状态为上传成功
            mediaFile.setFileStatus("301002");
            mediaFileRepository.save(mediaFile);
            //向MQ发送视频处理消息
            this.sendProcessVideoMsg(fileMd5);
            return new ResponseResult(CommonCode.SUCCESS);
    }
    //向MQ发送视频处理消息
    public ResponseResult sendProcessVideoMsg(String mediaId){
        Optional<MediaFile> byId = mediaFileRepository.findById(mediaId);
        if (!byId.isPresent()) {
            LOGGER.error("send videoMQ fail,not find mediaFile by " + mediaId);
            return new ResponseResult(CommonCode.FAIL);
        }
        MediaFile mediaFile = byId.get();
        Map map = new HashMap();
        map.put("mediaId",mediaId);
        String msg = JSON.toJSONString(map);
        try {
            this.rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingkey_media_video,
                    msg);
            LOGGER.info("send media process task msg:{}",msg);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.info("send media process task error,msg is:{},error:{}",msg,e.getMessage());
            return new ResponseResult(CommonCode.FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //将块文件合并
    public File mergeFile(File[] files,File mergeFile){
        RandomAccessFile write = null;
        try {
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Integer.parseInt(o1.getName())-Integer.parseInt(o2.getName());
                }
            });
            write = new RandomAccessFile(mergeFile,"rw");
            byte[] bys = new byte[1024];
            for (File file : files) {
                RandomAccessFile read = new RandomAccessFile(file,"r");
                int len = 0;
                while((len=read.read(bys))!=-1){
                    write.write(bys,0,len);
                }
                read.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (write!=null) {
                    write.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mergeFile;
    }
    //校验文件的md5值
    private boolean checkFileMd5(File mergeFile,String md5){
        if(mergeFile == null || StringUtils.isEmpty(md5)){
            return false;
        }
        //进行md5校验
        FileInputStream mergeFileInputstream = null;
        try {
            mergeFileInputstream = new FileInputStream(mergeFile);
        //得到文件的md5
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileInputstream);
        //比较md5
            if(md5.equalsIgnoreCase(mergeFileMd5)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                mergeFileInputstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
