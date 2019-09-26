package com.xuecheng.learning.client;

import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "XC-SEARCH-SERVICE")
public interface CourseSearchClient {
    @GetMapping("/search/getmedia/{id}")
    public TeachplanMediaPub getmedia(@PathVariable("id") String teachplanId);
}
