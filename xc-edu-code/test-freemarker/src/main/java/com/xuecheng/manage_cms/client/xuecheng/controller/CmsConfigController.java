package com.xuecheng.manage_cms.client.xuecheng.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/cms")
public class CmsConfigController {

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping("/config")
    public String config(Map<String,Object> map){
        ResponseEntity<Map> forEntity =
                restTemplate.getForEntity("http://localhost:31001/cms/config/getModel/5a791725dd573c3574ee333f",
                        Map.class);
        map.putAll(forEntity.getBody());
        return "lala";
    }
}
