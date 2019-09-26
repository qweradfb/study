package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsConfigTest {
    
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testRestTemplate(){
        ResponseEntity<Map> forEntity =
                restTemplate.getForEntity("http://localhost:31001/cms/config/getModel/5a791725dd573c3574ee333f",
                        Map.class);
        System.out.println(forEntity.getBody());
//        System.out.println(forEntity);
    }
    @Test
    public void Test(){
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(".");
        cmsPage.setPagePhysicalPath(".");
        cmsPage.setPageWebPath("dd");
        cmsPage.setHtmlFileId("..");
        cmsPageRepository.save(cmsPage);
    }
    @Test
    public void selectTest(){
        Optional<CmsPage> byId = cmsPageRepository.findById("5d71d696f897463694a1aff9");
        if (byId.isPresent()){
            CmsPage cmsPage = byId.get();
            System.out.println(cmsPage);
        }
    }
}
