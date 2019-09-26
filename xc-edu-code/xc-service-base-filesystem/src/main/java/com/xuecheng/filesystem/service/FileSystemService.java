package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.CoursePicRepository;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.exception.ExecptionCast;
import com.xuecheng.framework.model.response.ResponseResult;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class FileSystemService {
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    private int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    private int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    private String charset;
    @Value("${xuecheng.fastdfs.tracker_servers}")
    private String tracker_servers;

    @Autowired
    private FileSystemRepository fileSystemRepository;

    @Autowired
    private CoursePicRepository coursePicRepository;


    //上传图片
    public FileSystem upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata){
        if (multipartFile==null) ExecptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        if (StringUtils.isEmpty(businesskey)) ExecptionCast.cast(FileSystemCode.FS_UPLOADFILE_BUSINESSISNULL);
        Map map = null;
        try {
            map = JSON.parseObject(metadata, Map.class);
        }catch (Exception e){
            ExecptionCast.cast(FileSystemCode.FS_UPLOADFILE_METAERROR);
        }
        //初始化文件
        initFileSystem();
        //上传文件
        String fileId = upload(multipartFile);
        if (fileId==null) ExecptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        Optional<FileSystem> byId = fileSystemRepository.findById(fileId);
        if (byId.isPresent()){
            fileSystemRepository.deleteById(fileId);
        }
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setFileSize(multipartFile.getSize());
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileType(multipartFile.getContentType());
        fileSystem.setFiletag(filetag);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setMetadata(map);
        FileSystem save = fileSystemRepository.save(fileSystem);
        return save;
    }
    //初始化配置文件
    private void initFileSystem(){
        try {
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_charset(charset);
        }catch (Exception e){
            e.printStackTrace();
            ExecptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
    //上传文件
    private String upload(MultipartFile multipartFile){
        try {
            //创建客户端
            TrackerClient tc = new TrackerClient();
            //连接tracker Server
            TrackerServer ts = tc.getConnection();
            if (ts == null) {
                System.out.println("getConnection return null");
                return null;
            }
            //获取一个storage server
            StorageServer ss = tc.getStoreStorage(ts);
            if (ss == null) {
                System.out.println("getStoreStorage return null");
            }
            //创建一个storage存储客户端
            StorageClient1 sc1 = new StorageClient1(ts, ss);
            NameValuePair[] meta_list = null; //new NameValuePair[0];
            String suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")+1);
            String fileid = sc1.upload_file1(multipartFile.getBytes(), suffix, meta_list);
            System.out.println("fileid=" + fileid);
            return fileid;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //删除文件
    private int deleteFile (String group, String filePath) {
        TrackerServer trackerServer = null;
        StorageServer storageServer = null;

        //利用fastdfs客户端，实现文件上传到fastdfs服务器上
        try {
            //代码是模板式的
            //1、加载配置文件
//            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            initFileSystem();
            //2、创建一个tracker的客户端
            TrackerClient trackerClient = new TrackerClient();

            //3、通过trackerClient获取一个连接，连接到Tracker，得到一个TrackerServer
            trackerServer = trackerClient.getConnection();

            //4、通过trackerClient获取一个存储节点的StorageServer
            storageServer = trackerClient.getStoreStorage(trackerServer);

            //5、通过trackerServer和storageServer构造一个Storage客户端
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);

            //fastdfs删除文件
            return storageClient.delete_file(group, filePath);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭服务，释放资源
                if (null != storageServer) {
                    storageServer.close();
                }
                if (null != trackerServer) {
                    trackerServer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }


    //删除图片
    public ResponseResult deletePic(String courseId) {
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        String coursePicStr = null;
        if (byId.isPresent()){
            CoursePic coursePic = byId.get();
            coursePicStr = coursePic.getPic();
            try {
                deleteFile("group1", coursePicStr);
                coursePicRepository.deleteById(courseId);
                fileSystemRepository.deleteById(coursePicStr);
            }catch (Exception e){
                e.printStackTrace();
                try {
                    coursePicRepository.deleteById(courseId);
                }catch (Exception e1){
                    e1.printStackTrace();
                }finally {
                    try {
                        fileSystemRepository.deleteById(coursePicStr);
                    }catch (Exception e2){
                        e2.printStackTrace();
                    }
                }
                return new ResponseResult(FileSystemCode.FS_DELETEFILE_NOTEXISTS);
            }
        }else{
            try {
                deleteFile("group1", coursePicStr);
            }catch (Exception e1){
                e1.printStackTrace();
            }finally {
                try {
                    fileSystemRepository.deleteById(coursePicStr);
                }catch (Exception e2){
                    e2.printStackTrace();
                }
            }
            return new ResponseResult(FileSystemCode.FS_DELETEFILE_SERVERFAILTRUE);
        }
            return new ResponseResult(FileSystemCode.FS_DELETEFILE_SUCCESS);
    }
}
