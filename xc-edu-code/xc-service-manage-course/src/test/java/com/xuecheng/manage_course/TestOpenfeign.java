package com.xuecheng.manage_course;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOpenfeign {
    
    @Autowired
    CmsPageClient cmsPageClient;
    
    @Test
    public void cmsPageClientTest(){
        CmsPage cmsPage = cmsPageClient.findById("5a795ac7dd573c04508f3a56");
        System.out.println(cmsPage);
    }

    @Test
    public void saveTest(){
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("11");
        cmsPage.setPagePhysicalPath("22");
        cmsPage.setSiteId("22");
        cmsPage.setPageWebPath("haha");
        CmsPageResult save = cmsPageClient.save(cmsPage);
        System.out.println(save);
    }
}
