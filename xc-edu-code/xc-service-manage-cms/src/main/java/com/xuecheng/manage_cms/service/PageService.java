package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExecptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.catalina.connector.Request;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class PageService {

    private static Logger logger = LoggerFactory.getLogger(PageService.class);

    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFSBucket gridFSBucket;
    @Autowired
    private RabbitTemplate rabbitTemplate;



    //获取所有页面
    public QueryResult findList(int page, int size, QueryPageRequest queryPageRequest) {
        if (queryPageRequest == null) queryPageRequest  =  new QueryPageRequest();
        if (page<=0) page = 1;
        page = page-1;
        ExampleMatcher exampleMatcher = ExampleMatcher.matching();
        exampleMatcher = exampleMatcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        exampleMatcher = exampleMatcher.withMatcher("pageType",ExampleMatcher.GenericPropertyMatchers.contains());
        exampleMatcher = exampleMatcher.withMatcher("pageName",ExampleMatcher.GenericPropertyMatchers.contains());

        CmsPage cmsPage = new CmsPage();
        String pageAliase = queryPageRequest.getPageAliase();
        if(StringUtils.isNotEmpty(pageAliase)) cmsPage.setPageAliase(pageAliase);
        String pageName = queryPageRequest.getPageName();
        if(StringUtils.isNotEmpty(pageName)) cmsPage.setPageName(pageName);
        String pageType = queryPageRequest.getPageType();
        if(StringUtils.isNotEmpty(pageType)) cmsPage.setPageType(pageType);
        String siteId = queryPageRequest.getSiteId();
        if(StringUtils.isNotEmpty(siteId)) cmsPage.setSiteId(siteId);
        Example example = Example.of(cmsPage,exampleMatcher);
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());
        return queryResult;
    }

    //获取所有站点信息
    public QueryResult siteList() {
        List<Map> maps = new ArrayList<>();
        List<CmsSite> all = cmsSiteRepository.findAll();
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all);
        return queryResult;
    }

    //添加页面
    public CmsPageResult add(CmsPage cmsPage) {
        if (cmsPage.getSiteId()==null|| cmsPage.getPageWebPath()==null||cmsPage.getPageName()==null)
            ExecptionCast.cast(CommonCode.REQUEST_PARAM_NULL);
        //判断页面是否存在
        CmsPage byPageNameAndSiteIdAndPageWebPath = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (byPageNameAndSiteIdAndPageWebPath!=null){
            ExecptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        if (byPageNameAndSiteIdAndPageWebPath==null){
            cmsPage.setPageId(null); //保证让主键id自动生成
            cmsPageRepository.save(cmsPage); //返回结果回自动封装到cmsPage中
            return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        }
        return new CmsPageResult(CommonCode.FAIL,null);

    }

    public QueryResult templateList() {
        List<Map> maps = new ArrayList<>();
        List<CmsTemplate> all = cmsTemplateRepository.findAll();
        QueryResult queryResult = new QueryResult();
        queryResult.setList(all);
        return queryResult;
    }

    //根据id查询页面
    public CmsPage findPageById(String id) {
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        if (byId.isPresent()&&StringUtils.isNotEmpty(id)){
            CmsPage cmsPage = byId.get();
            return cmsPage;
        }
        return null;
    }

    //编辑页面
    public ResponseResult edit(String id,CmsPage cmsPage) {
        if ("".equals(id)||cmsPage==null){
            ExecptionCast.cast(CommonCode.REQUEST_PARAM_NULL);
        }
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        if (byId.isPresent()){
            CmsPage page = byId.get();
            if (cmsPage!=null){
                page.setSiteId(cmsPage.getSiteId());
                page.setTemplateId(cmsPage.getTemplateId());
                page.setPageName(cmsPage.getPageName());
                page.setPageAliase(cmsPage.getPageAliase());
                page.setPageWebPath(cmsPage.getPageWebPath());
                page.setPageParameter(cmsPage.getPageParameter());
                page.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
                page.setPageType(cmsPage.getPageType());
                page.setPageCreateTime(cmsPage.getPageCreateTime());
                page.setDataUrl(cmsPage.getDataUrl());
            }
            CmsPage save = cmsPageRepository.save(page);
            if (save!=null) return new ResponseResult(CommonCode.SUCCESS);
        }

        return new ResponseResult(CommonCode.FAIL);
    }

    //根据id删除页面
    public ResponseResult deleteById(String id) {
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        if (byId.isPresent()){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        ExecptionCast.cast(CmsCode.CMS_DELETEPAGE_NOTEXISTS);
        return new ResponseResult(CommonCode.FAIL);
    }

    //页面静态化
    public String getPageHtml(String pageId,HttpServletRequest request){
        //判断页面是否存在
        CmsPage cmsPage = this.findPageById(pageId);
        if (cmsPage==null) ExecptionCast.cast(CmsCode.CMS_DELETEPAGE_NOTEXISTS);
        //获取页面模型
        Map model = getModel(cmsPage, request);
        //获取页面模板
        String template = getTemplate(cmsPage);
        //页面静态化
        String html = getGeneralHtml(model,template);
        return html;
    }

    private String getGeneralHtml(Map model, String templateStr) {
        try {
            //创建配置类
            Configuration configuration=new Configuration(Configuration.getVersion());
            //模板加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template",templateStr);
            configuration.setTemplateLoader(stringTemplateLoader);
            //得到模板
            Template template = configuration.getTemplate("template","utf-8");
            //静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            if (content==null) ExecptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
            return content;
        }catch (Exception e){
            ExecptionCast.cast(CmsCode.CMS_GENERATEHTML_SAVEHTMLERROR);
        }

        return null;
    }

    private String getTemplate(CmsPage cmsPage) {
        try {
            String templateId = cmsPage.getTemplateId();
            Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
            if (optional.isPresent()){
                CmsTemplate cmsTemplate = optional.get();
                String templateFileId = cmsTemplate.getTemplateFileId();
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            String s = IOUtils.toString(gridFsResource.getInputStream());
            return s;
            }
        }catch (Exception e){
            e.printStackTrace();
            ExecptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        return null;
    }

    private Map getModel(CmsPage cmsPage, HttpServletRequest request) {
        String dataUrl = cmsPage.getDataUrl();
        if (dataUrl==null) ExecptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
//        ResponseEntity<Map> forEntity =
//                restTemplate.getForEntity(dataUrl,
//                        Map.class);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        String authorization = request.getHeader("Authorization");

        if (authorization!=null) {
            headers.add("Authorization", authorization);
        }
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new
                HttpEntity<>(headers);
        ResponseEntity<Map> exchange = restTemplate.exchange(dataUrl, HttpMethod.GET,
                multiValueMapHttpEntity, Map.class);
        return exchange.getBody();
    }

    //发布页面
    public void postPage(String pageId,HttpServletRequest request){
        CmsPage cmsPage = null;
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (optional.isPresent()){
            cmsPage = optional.get();
        }else{
            ExecptionCast.cast(CmsCode.CMS_DELETEPAGE_NOTEXISTS);
        }
        //页面静态化
        String pageHtml = getPageHtml(pageId,request);
        //将文件保存到文件服务器
        saveFile(cmsPage,pageHtml);
        //给消费方发送消息
        sendMessage(cmsPage);
    }

    private void sendMessage(CmsPage cmsPage) {
        Map<String,Object> map  = new HashMap<>();
        map.put("pageId",cmsPage.getPageId());
        String message = JSON.toJSONString(map);
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, cmsPage.getSiteId(),message);
    }

    private void saveFile(CmsPage cmsPage, String pageHtml) {
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isNotEmpty(htmlFileId)){
            //如果存在就删除
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        try {
            InputStream inputStream = IOUtils.toInputStream(pageHtml, "utf-8");
            ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName(), "");
            cmsPage.setHtmlFileId(objectId.toString());
            cmsPageRepository.save(cmsPage);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //保存页面
    public CmsPageResult save(CmsPage cmsPage) {
        if (cmsPage.getSiteId()==null|| cmsPage.getPageWebPath()==null||cmsPage.getPageName()==null)
            ExecptionCast.cast(CommonCode.REQUEST_PARAM_NULL);
        //判断页面是否存在
        CmsPage byPageNameAndSiteIdAndPageWebPath = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (byPageNameAndSiteIdAndPageWebPath==null){
            cmsPage.setPageId(null); //保证让主键id自动生成
        }else{
            cmsPage.setPageId(byPageNameAndSiteIdAndPageWebPath.getPageId());
        }
        cmsPageRepository.save(cmsPage); //返回结果回自动封装到cmsPage中
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
    }

    //一键发布
    public CmsPostPageResult postPageQuick(CmsPage cmsPage,HttpServletRequest request) {
        //保存页面基本信息
        CmsPageResult cmsPageResult = this.save(cmsPage);
        if (!cmsPageResult.isSuccess())  return new CmsPostPageResult(CommonCode.FAIL,null);
        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        //发布页面（将页面保存到文件服务器，并向客户端发消息）
        this.postPage(cmsPage1.getPageId(),request);
        Optional<CmsSite> byId = cmsSiteRepository.findById(cmsPage1.getSiteId());
        CmsSite cmsSite = null;
        if (byId.isPresent()) {
            cmsSite = byId.get();
        }else {
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        String siteDomain = cmsSite.getSiteDomain();
        String siteWebPath = cmsSite.getSiteWebPath();
        String pageWebPath = cmsPage1.getPageWebPath();
        String pageName = cmsPage1.getPageName();
        String url = siteDomain+siteWebPath+pageWebPath+pageName;
        return new CmsPostPageResult(CommonCode.SUCCESS,url);
    }
}
