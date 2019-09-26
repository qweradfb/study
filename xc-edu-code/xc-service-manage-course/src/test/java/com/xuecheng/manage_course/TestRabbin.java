package com.xuecheng.manage_course;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.xuecheng.framework.domain.cms.CmsSite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRabbin {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void rabbinTest() {

        for (int i = 0; i < 10; i++) {
            ResponseEntity<Map> forEntity = restTemplate
                    .getForEntity("http://XC-SERVICE-MANAGE-CMS/cms/siteList", Map.class);
            Map map = forEntity.getBody();
            System.out.println(map);
        }
    }

    @Test
    public void cmsTest() {
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://127.0.0.1:31001/cms/siteList", Map.class);
        Map body = forEntity.getBody();
        System.out.println(body);
    }
}
