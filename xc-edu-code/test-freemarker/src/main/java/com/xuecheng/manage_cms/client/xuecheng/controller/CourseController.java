package com.xuecheng.manage_cms.client.xuecheng.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/template")
public class CourseController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/view/{id}")
    public String getView(@PathVariable("id") String id,Map<String,Object> map){
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31200/course/view/" + id, Map.class);
        Map body = forEntity.getBody();
        map.putAll(body);
        return "course";
    }
}
