package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.manage_cms.service.PageService;
import org.aspectj.lang.annotation.AfterThrowing;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageTest {
    @Autowired
    PageService pageService;

    @Test
    public void saveTest(){
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("11");
        cmsPage.setPagePhysicalPath("guagua");
        cmsPage.setSiteId("22");
        cmsPage.setPageWebPath("guagua");
        CmsPageResult save = pageService.save(cmsPage);
        System.out.println(save);
    }
}
