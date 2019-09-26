package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Test
    public void findAllTest(){
        List<CmsPage> all = cmsPageRepository.findAll();
        System.out.println(all);
    }

    @Test
    public void findPageTest(){
        Pageable pageable = PageRequest.of(0,10);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all.getTotalPages());
        System.out.println(all.getTotalElements());
        System.out.println(all.getContent());
    }

    @Test
    public void addTest(){
        CmsPage cmsPage = new CmsPage();
        cmsPage.setDataUrl("dsf");
        cmsPage.setHtmlFileId("sdf");
        cmsPageRepository.insert(cmsPage);
    }

    @Test
    public void deleteByIdTest(){
        cmsPageRepository.deleteById("5d5a4d8c915790310851d8fb");
    }
    
    @Test
    public void updateTest(){
        Optional<CmsPage> cmsPage = cmsPageRepository.findById("5d5a50dc91579008bccc45dc");
        if (cmsPage.isPresent()) {
            CmsPage cmsPage1 = cmsPage.get();
            cmsPage1.setHtmlFileId("dsf");
            cmsPage1.setPageCreateTime(new Date());
            cmsPage1.setPageName("dsf");
            cmsPageRepository.save(cmsPage1);
        }
    }

    @Test
    public void queryStringTest(){
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageAliase("首");
        Example example = Example.of(cmsPage,exampleMatcher);
        Pageable pageable = PageRequest.of(0,10);
        Page all = cmsPageRepository.findAll(example, pageable);
        System.out.println(all.getContent());
    }

    @Test
    public void sideTest(){
        List<CmsSite> all = cmsSiteRepository.findAll();
        for (CmsSite cmsSite : all) {
            System.out.println(cmsSite.getSiteId());
        }
    }

    @Test
    public void adddTest(){
        CmsPage byPageNameAndSiteIdAndPageWebPath = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath("index.html", "5a751fab6abb5044e0d19ea1", "/index.html");
        System.out.println(byPageNameAndSiteIdAndPageWebPath);
    }


    

}
