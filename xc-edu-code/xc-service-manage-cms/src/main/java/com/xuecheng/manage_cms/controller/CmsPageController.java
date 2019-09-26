package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms")
public class CmsPageController extends BaseController implements CmsPageControllerApi {

    @Autowired
    private PageService pageService;

    @GetMapping("/page/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page,@PathVariable("size") int size, QueryPageRequest queryPageRequest) {
        ResultCode resultCode = CommonCode.SUCCESS;
        QueryResult queryResult = pageService.findList(page, size, queryPageRequest);
        QueryResponseResult result = new QueryResponseResult(resultCode,queryResult);
        return result;
    }

    @GetMapping("/siteList")
    public QueryResponseResult siteList(){
        ResultCode resultCode = CommonCode.SUCCESS;
        QueryResult queryResult = pageService.siteList();
        QueryResponseResult result = new QueryResponseResult(resultCode,queryResult);
        return result;
    }

    @GetMapping("/templateList")
    public QueryResponseResult templateList(){
        ResultCode resultCode = CommonCode.SUCCESS;
        QueryResult queryResult = pageService.templateList();
        QueryResponseResult result = new QueryResponseResult(resultCode,queryResult);
        return result;
    }

    @PostMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        CmsPageResult cmsPageResult = pageService.add(cmsPage);
        return cmsPageResult;
    }

    @GetMapping("/echo")
    public CmsPage echo(String id) {
        CmsPage cmsPage = pageService.findPageById(id);
        return cmsPage;
    }

    @PutMapping("/edit/{id}")
    public ResponseResult edit(@PathVariable("id") String id,@RequestBody CmsPage cmsPage) {
        ResponseResult responseResult = pageService.edit(id,cmsPage);
        return responseResult;
    }

    @DeleteMapping("/delete/{id}")
    public ResponseResult delete(@PathVariable String id) {
        ResponseResult responseResult = pageService.deleteById(id);
        return responseResult;
    }

    @PutMapping("/postPage/{id}")
    public ResponseResult postPage(@PathVariable("id") String pageId) {
        try {
            pageService.postPage(pageId,request);
            return new ResponseResult(CommonCode.SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    @GetMapping("/page/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        CmsPage cmsPage = pageService.findPageById(id);
        return cmsPage;
    }


    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return pageService.save(cmsPage);
    }

    //    一键发布页面
    @PostMapping("/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {
        CmsPostPageResult cmsPostPageResult = pageService.postPageQuick(cmsPage,request);
        return cmsPostPageResult;
    }


}
