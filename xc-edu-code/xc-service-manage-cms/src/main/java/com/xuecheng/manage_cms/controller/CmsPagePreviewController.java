package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExecptionCast;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/cms")
public class CmsPagePreviewController extends BaseController {

    @Autowired
    private PageService pageService;

    @GetMapping("/preview/{pageId}")
    public void preview(@PathVariable("pageId") String pageId){
        response.setHeader("Content-type","text/html;charset=utf-8");
        String pageHtml = pageService.getPageHtml(pageId,request);
        if (pageHtml!=null){
            try {
                response.getOutputStream().write(pageHtml.getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            ExecptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
    }
}
