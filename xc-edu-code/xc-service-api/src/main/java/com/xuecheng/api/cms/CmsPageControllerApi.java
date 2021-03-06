package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value="cms页面管理接口",description = "cms页面管理接口，提供页面的增、删、改、查")
public interface CmsPageControllerApi {
    @ApiOperation("分页查询页面列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value = "页码",required=true,paramType="path",dataType="int"),
                    @ApiImplicitParam(name="size",value = "每页记录数",required=true,paramType="path",dataType="int")
                    })
    QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);
    QueryResponseResult siteList();
    QueryResponseResult templateList();
    @ApiOperation("新增页面")
    CmsPageResult add(CmsPage cmsPage);
    @ApiOperation("回显页面")
    CmsPage echo(String id);
    @ApiOperation("编辑页面")
    ResponseResult edit(String id,CmsPage cmsPage);
    @ApiOperation("根据id删除页面")
    ResponseResult delete(String id);
    @ApiOperation("发布页面")
    ResponseResult postPage(String pageId);
    @ApiOperation("根据id查询页面")
    CmsPage findById(String id);
    @ApiOperation("保存页面")
    CmsPageResult save(CmsPage cmsPage);
    @ApiOperation("一键发布页面")
    CmsPostPageResult postPageQuick(CmsPage cmsPage);

}
