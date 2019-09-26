package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.manage_course.dao.CategoryNodeMapper;
import com.xuecheng.manage_course.dao.CoursePubRepository;
import com.xuecheng.manage_course.dao.TeachplanMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CourseServiceTest {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private CategoryNodeMapper categoryNodeMapper;
    @Autowired
    private CoursePubRepository coursePubRepository;
    @Test
    public void teachplanTest(){
        TeachplanNode teachplanById = teachplanMapper.findTeachplanById("4028e581617f945f01617f9dabc40000");
        System.out.println(teachplanById);
    }
    @Test
    public void categoryTest(){
        CategoryNode categoryNode = categoryNodeMapper.findCategoryNode();
        System.out.println(categoryNode);
    }
    @Test
    public void coursePubTest(){
        Optional<CoursePub> byId = coursePubRepository.findById("297e7c7c62b888f00162b8a965510001");
        if (byId.isPresent()){
            System.out.println(byId.get());
        }
    }
}
