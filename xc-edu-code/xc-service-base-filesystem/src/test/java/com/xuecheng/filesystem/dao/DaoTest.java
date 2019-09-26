package com.xuecheng.filesystem.dao;

import com.sun.corba.se.impl.orb.ParserTable;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {

    @Autowired
    CoursePicRepository coursePicRepository;
    
    @Autowired
    FileSystemService fileSystemService;

    @Test
    public void Test(){
        coursePicRepository.deleteById("1212");
    }
    @Test
    public void serivceTest(){
        fileSystemService.deletePic("group1/M00/00/01/wKgZmV1t0i-AB1QiAAG_AU5QNvI756.jpg");
    }
    @Test
    public void uploadTest(){
        System.out.println(FileSystemCode.FS_DELETEFILE_SUCCESS.message());
//        System.out.println(CourseCode.COURSE_NOTFIND.message());
    }
}
