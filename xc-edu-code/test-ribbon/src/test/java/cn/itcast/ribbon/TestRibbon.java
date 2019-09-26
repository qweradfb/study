package cn.itcast.ribbon;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRibbon {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Test
    public void ribbonTest() {
        //服务id
        String serviceId = "xc-service-manage-cms";
        for (int i = 0; i < 10; i++) {
//通过服务id调用
            ResponseEntity<CmsSite> forEntity = restTemplate.getForEntity("http://" + serviceId
                    + "/cms/siteList", CmsSite.class);
            CmsSite cmsSite = forEntity.getBody();
            System.out.println(cmsSite);
        }
    }
}
