package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.model.request.RequestData;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.Oauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi{


    private static Logger logger = LoggerFactory.getLogger(CourseController.class);
    @Autowired
    private CourseService courseService;

    //查看课程计划
    @PreAuthorize("hasAuthority('lala')")
    @GetMapping("/teachplan/list/{id}")
    public TeachplanNode findTeachplanList(@PathVariable("id") String courseId) {
        return courseService.findTeachplanById(courseId);
    }

    //添加课程计划
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        try {
            courseService.addTeachplan(teachplan);
            return new ResponseResult(CommonCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }


    }

    //删除课程计划
    @DeleteMapping("/teachplan/delete/{id}")
    public ResponseResult deleteTeachplan(@PathVariable("id") String id) {
        try {
            courseService.deleteTeachplan(id);
            return new  ResponseResult(CommonCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    //回显课程计划
    @PutMapping("/teachplan/echo/{id}")
    public Teachplan echoTeachplan(@PathVariable("id") String id) {
        Teachplan teachplan = courseService.echoTeachplan(id);
        return teachplan;
    }

    //编辑课程计划
    @PostMapping("/teachplan/edit")
    public ResponseResult editTeachplan(@RequestBody Teachplan teachplan) {
        try {
            courseService.editTeachplan(teachplan);
            return new ResponseResult(CommonCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    //分页查看所有课程
    @PreAuthorize("hasAuthority('xc_teachmanager_course_base')")
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findAllCourse(@PathVariable("page") Integer page, @PathVariable("size") Integer size, RequestData requestData){
        QueryResult queryResult = null;
        try {
            Map<String, String> jwtClaimsFromHeader = Oauth2Util.getJwtClaimsFromHeader(request);
            String companyId = jwtClaimsFromHeader.get("companyId");
            queryResult = courseService.findAllCourse(page, size, requestData,companyId);
            return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        }catch (Exception e){
            e.printStackTrace();
            return new QueryResponseResult(CommonCode.FAIL,queryResult);
        }
    }

    //查询课程分类
    @GetMapping("/category/list")
    public CategoryNode getCategoryNode(){
        CategoryNode categoryNode = courseService.getCategoryNode();
        return categoryNode;
    }

    //添加课程
    @PostMapping("/coursebase/add")
    public ResponseResult addCourse(@RequestBody CourseBase courseBase) {
        try {
            courseService.addCourse(courseBase);
            return new ResponseResult(CommonCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }

    }

    //回显课程
    @GetMapping("/coursebase/echo/{id}")
    public CourseBase echoCourse(@PathVariable("id") String id) {
        CourseBase courseBase = courseService.echoCoursebase(id);
        return courseBase;
    }

    //更新课程
    @PutMapping("/coursebase/exit/{id}")
    public ResponseResult updateCourse(@PathVariable("id") String id,@RequestBody CourseBase courseBase) {
        try {
            courseService.updateCouser(id,courseBase);
            return new ResponseResult(CommonCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    //删除课程
    @DeleteMapping("/coursebase/delete/{id}")
    public ResponseResult deleteCourse(@PathVariable("id") String id) {
        try {
            courseService.deleteCourse(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }

    }

    //回显课程营销
    @GetMapping("/courseMarket/echo/{id}")
    public CourseMarket echoCourseMarket(@PathVariable("id") String id) {
        CourseMarket courseMarket = courseService.echoCourseMarket(id);
        return courseMarket;
    }

    //编辑课程营销
    @PostMapping("/courseMarket/exit/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {
        try {
            courseService.editCourseMarket(id,courseMarket);
            return new ResponseResult(CommonCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
    }
    //保存上传图片信息
    @PostMapping("/coursepic/add")
    public ResponseResult addPic(String courseId, String pic) {
        try {
            courseService.addPic(courseId,pic);
            return new ResponseResult(FileSystemCode.FS_UPLOADFILE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(FileSystemCode.FS_UPLOADFILE_FILE);
        }
    }

    //显示图片
    @GetMapping("/coursepic/list/{id}")
    public CoursePic getPicList(@PathVariable("id") String courseId) {
        CoursePic coursePic = courseService.getPicList(courseId);
        return coursePic;
    }

    //课程视图查询
    @GetMapping("/view/{id}")
    public CourseView courseView(@PathVariable("id") String id) {
        CourseView courseView = courseService.courseView(id);
        return courseView;
    }

    //预览课程
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        CoursePublishResult coursePublishResult = courseService.preview(id);
        return coursePublishResult;
    }


    //查看课程发布状态
    @GetMapping("/courseView/{id}")
    public CourseBase queryCourseStatus(@PathVariable("id") String id){
        CourseBase courseBase = courseService.findCourseStatus(id);
        return courseBase;
    }

    //发布课程
    @PostMapping("/publish/{id}")
    public CmsPostPageResult publish(@PathVariable("id") String id) {
        CmsPostPageResult cmsPostPageResult = courseService.publish(id);
        return cmsPostPageResult;
    }

    @PostMapping("/savemedia")
    public ResponseResult savemedia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.savemedia(teachplanMedia);
    }


}
