package com.xuecheng.search.controller;

import com.xuecheng.api.serarch.EsCourseControllerApi;
import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.search.service.EsCourseMediaService;
import com.xuecheng.search.service.EsCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
public class EsCourseController implements EsCourseControllerApi {

    @Autowired
    private EsCourseService esCourseService;

    @Autowired
    private EsCourseMediaService esCourseMediaService;

    @GetMapping("/course/{page}/{size}")
    public QueryResponseResult<CoursePub> list(@PathVariable("page") int page, @PathVariable("size") int size, CourseSearchParam courseSearchParam) throws IOException {
        QueryResponseResult result = esCourseService.list(page,size,courseSearchParam);
        return result;
    }

    //根据id查询课程信息
    @GetMapping("/getall/{id}")
    public Map<String, CoursePub> getAll(@PathVariable("id") String id) {
        return esCourseService.getAll(id);
    }

    //根据课程信息查询课程的媒资信息

    @GetMapping("/getmedia/{id}")
    public TeachplanMediaPub getmedia(@PathVariable("id") String teachplanId) {
        List<String> ids = new ArrayList<>();
        ids.add(teachplanId);
        List<TeachplanMediaPub> getmedia = esCourseMediaService.getmedia(ids);
        TeachplanMediaPub teachplanMediaPub = getmedia.get(0);
        return teachplanMediaPub;
    }
}
