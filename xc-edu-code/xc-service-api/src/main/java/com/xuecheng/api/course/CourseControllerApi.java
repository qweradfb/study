package com.xuecheng.api.course;

import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.request.RequestData;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

@Api(value="课程管理")
public interface CourseControllerApi {
    @ApiOperation("课程计划查询")
    TeachplanNode findTeachplanList(String courseId);
    @ApiOperation("课程计划添加")
    ResponseResult addTeachplan(Teachplan teachplan);
    @ApiOperation("课程计划删除")
    ResponseResult deleteTeachplan(String id);
    @ApiOperation("课程计划回显")
    Teachplan echoTeachplan(String id);
    @ApiOperation("课程计划编辑")
    ResponseResult editTeachplan(Teachplan teachplan);
    @ApiOperation("课程查询")
    QueryResponseResult findAllCourse(Integer page,Integer size, RequestData requestData);
    @ApiOperation("课程分类查询")
    CategoryNode getCategoryNode();
    @ApiOperation("添加课程")
    ResponseResult addCourse(CourseBase courseBase);
    @ApiOperation("回显课程")
    CourseBase echoCourse(String id);
    @ApiOperation("更新课程")
    ResponseResult updateCourse(String id,CourseBase courseBase);
    @ApiOperation("删除课程")
    ResponseResult deleteCourse(String id);
    @ApiOperation("回显课程营销")
    CourseMarket echoCourseMarket(String id);
    @ApiOperation("更新课程营销")
    ResponseResult updateCourseMarket(String id,CourseMarket courseMarket);
    @ApiOperation("保存上传的图片")
    ResponseResult addPic(String courseId,String pic);
    @ApiOperation("课程视图查询")
    CourseView courseView(String id);
    @ApiOperation("预览课程")
    CoursePublishResult preview(String id);
    @ApiOperation("发布课程")
    CmsPostPageResult publish(String id);
    @ApiOperation("保存媒资信息")
    ResponseResult savemedia(TeachplanMedia teachplanMedia);



}
