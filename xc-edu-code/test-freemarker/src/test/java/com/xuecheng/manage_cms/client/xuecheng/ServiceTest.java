package com.xuecheng.manage_cms.client.xuecheng;

import com.xuecheng.manage_cms.client.xuecheng.service.CmsConfigService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ServiceTest {
    
    @Autowired
    private CmsConfigService cmsConfigService;
    
    @Test
    public void cmsConfigTest(){
        cmsConfigService.getPageHtml();
    }
    
}
