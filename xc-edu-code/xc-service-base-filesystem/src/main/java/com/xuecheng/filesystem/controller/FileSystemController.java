package com.xuecheng.filesystem.controller;

import com.xuecheng.api.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {

    @Autowired
    private FileSystemService fileSystemService;

    //上传图片
    @PostMapping("/upload")
    public UploadFileResult upload(MultipartFile multipartFile, String filetag, String businesskey, String metadata) {
        try {
            FileSystem upload = fileSystemService.upload(multipartFile, filetag, businesskey, metadata);
            return new UploadFileResult(FileSystemCode.FS_UPLOADFILE_SUCCESS,upload);
        }catch (Exception e){
            e.printStackTrace();
            return new UploadFileResult(FileSystemCode.FS_UPLOADFILE_FILE,null);
        }
    }
    //删除图片
    @DeleteMapping("/delete/{courseId}")
    public ResponseResult deletePic(@PathVariable("courseId") String courseId) {
        ResponseResult  responseResult = fileSystemService.deletePic(courseId);
        return responseResult;
    }




}
