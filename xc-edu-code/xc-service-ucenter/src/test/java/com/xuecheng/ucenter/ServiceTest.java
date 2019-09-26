package com.xuecheng.ucenter;

import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.PassThroughExceptionTranslationStrategy;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ServiceTest {
    @Autowired
    private XcUserRepository xcUserRepository;

    @Test
    public void Test(){
        XcUser admin = xcUserRepository.findXcUserByUsername("admin");
        System.out.println(admin);
    }
}
