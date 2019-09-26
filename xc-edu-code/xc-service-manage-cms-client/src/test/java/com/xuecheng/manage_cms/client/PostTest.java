package com.xuecheng.manage_cms.client;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.client.dao.CmsPageRepository;
import com.xuecheng.manage_cms.client.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PostTest {

    @Autowired
    CmsPageRepository cmsPageRepository;
    
    @Autowired
    private PageService pageService;

    @Test
    public void postPageTest(){
        pageService.savePageToServerPath("5a795ac7dd573c04508f3a56");
    }

    @Test
    public void Test(){
        Optional<CmsPage> byId = cmsPageRepository.findById("5a795ac7dd573c04508f3a56");
        if (byId.isPresent()){
            CmsPage cmsPage = byId.get();
            System.out.println();
        }
    }
    
    
}
