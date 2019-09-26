package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.manage_course.dao.CourseBaseMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CourseMapperTest {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    
    @Test
    public void courseTest(){
        long l = courseBaseMapper.updateSatus("2977c7c62b888f00162b8a7dec20000", "1");
        System.out.println(l);
    }
}
