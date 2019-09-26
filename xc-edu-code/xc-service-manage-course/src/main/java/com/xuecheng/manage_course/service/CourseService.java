package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.netflix.discovery.converters.Auto;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExecptionCast;
import com.xuecheng.framework.model.request.RequestData;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class CourseService {

    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private TeachplanRepository teachplanRepository;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private CourseBaseMapper courseBaseMapper;

    @Autowired
    private CategoryNodeMapper categoryNodeMapper;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    @Autowired
    private CourseMarketMapper courseMarketMapper;

    @Autowired
    private CoursePicRepository coursePicRepository;

    @Autowired
    private CmsPageClient cmsPageClient;

    @Autowired
    private CoursePubRepository coursePubRepository;

    @Autowired
    private TeachplanMediaRepository teachplanMediaRepository;

    @Autowired
    private TeachplanMediaPubRepository teachplanMediaPubRepository;


    //根据id查询所有课程计划
    public TeachplanNode findTeachplanById(String id){
        return teachplanMapper.findTeachplanById(id);
    }


    //添加课程计划
    public void addTeachplan(Teachplan teachplan) {
        //判断课程是否存在
        Optional<CourseBase> optional = courseBaseRepository.findById(teachplan.getCourseid());
        CourseBase courseBase = null;
        if (!optional.isPresent()){
            ExecptionCast.cast(CourseCode.COURSE_NOTFIND);
        }else {
            courseBase = optional.get();
        }
        String courseid = teachplan.getCourseid();
        //判读parentId是否存在如果不存在就创建
        Teachplan parentTea = teachplanRepository.findByCourseidAndParentid(courseid, "0");
        if (parentTea==null){
            parentTea = new Teachplan();
            parentTea.setCourseid(courseid);
            parentTea.setPname(courseBase.getName());
            parentTea.setParentid("0");
            parentTea.setGrade("1");
            parentTea.setOrderby(1);
            parentTea.setStatus("0");
            parentTea = teachplanRepository.save(parentTea);
        }
        String parentid = teachplan.getParentid();
        if (parentid==null){
            teachplan.setParentid(parentTea.getId());
            teachplan.setGrade("2");
        }else{
            Optional<Teachplan> parentOption = teachplanRepository.findById(teachplan.getParentid());
            if (parentOption.isPresent()){
                Teachplan teachplan1 = parentOption.get();
                int i = Integer.parseInt(teachplan1.getGrade());
                teachplan.setGrade(i+1+"");
            }
        }

        teachplanRepository.save(teachplan);
    }

    //删除课程计划
    public void deleteTeachplan(String id) {
        teachplanMapper.deleteTeachplan(id);
    }

    //回显课程计划
    public Teachplan echoTeachplan(String id) {
        Teachplan teachplan = null;
        Optional<Teachplan> optional = teachplanRepository.findById(id);
        if (optional.isPresent()){
            teachplan = optional.get();
        }
        return teachplan;
    }

    //编辑课程计划
    public void editTeachplan(Teachplan teachplan) {
        teachplanRepository.save(teachplan);
    }

    //分页查询所有课程
    public QueryResult findAllCourse(Integer page, Integer size, RequestData requestData,String companyId) {
        PageHelper.startPage(page,size);
        Page<CourseBase> courseBases = courseBaseMapper.findAllCourse(companyId);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(courseBases.getResult());
        queryResult.setTotal(courseBases.getTotal());
        return queryResult;
    }

    //获取课程分类
    public CategoryNode getCategoryNode() {
        CategoryNode categoryNode = categoryNodeMapper.findCategoryNode();
        return categoryNode;
    }

    //添加课程
    public void addCourse(CourseBase courseBase) {
        courseBase.setStatus("202001");
        CourseBase save = courseBaseRepository.save(courseBase);

        Teachplan teachplan = new Teachplan();
        teachplan.setPname(save.getName());
        teachplan.setParentid("0");
        teachplan.setGrade("1");
        teachplan.setDescription(save.getDescription());
        teachplan.setCourseid(save.getId());
        teachplan.setStatus("0");
        teachplanRepository.save(teachplan);
    }

    //回显课程
    public CourseBase echoCoursebase(String id) {
        Optional<CourseBase> byId = courseBaseRepository.findById(id);
        if (byId.isPresent()){
            CourseBase courseBase = byId.get();
            return courseBase;
        }
        return null;
    }

    //编辑课程
    public void updateCouser(String id, CourseBase courseBase) {
        Optional<CourseBase> byId = courseBaseRepository.findById(id);
        if (!byId.isPresent()) ExecptionCast.cast(CommonCode.FAIL);
        courseBaseRepository.save(courseBase);
    }

    //删除课程
    public void deleteCourse(String id) {
        teachplanRepository.deleteByCourseid(id);
        courseBaseRepository.deleteById(id);
    }

    //回显课程营销
    public CourseMarket echoCourseMarket(String id) {
        Optional<CourseMarket> byId = courseMarketRepository.findById(id);
        if (byId.isPresent()) {
            CourseMarket courseMarket = byId.get();
            return courseMarket;
        }
        return null;
    }

    //编辑课程营销
    public void editCourseMarket(String id, CourseMarket courseMarket) {
        Optional<CourseMarket> byId = courseMarketRepository.findById(id);
        if (byId.isPresent()){
            courseMarketRepository.save(courseMarket);
        }else{
            courseMarketMapper.addCourseMarket(courseMarket);
        }
    }

    //上传图片
    public void addPic(String courseId, String pic) {
        CoursePic coursePic = null;
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        if (byId.isPresent()){
            coursePic = byId.get();
        }
        if (coursePic ==null) coursePic = new CoursePic();
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        Optional<CourseBase> byId1 = courseBaseRepository.findById(courseId);
        if (byId1.isPresent()){
            CourseBase courseBase = byId1.get();
            courseBase.setPic(pic);
            courseBaseRepository.save(courseBase);
        }

    }

    //获取图片列表
    public CoursePic getPicList(String courseId) {
        CoursePic coursePic = null;
        Optional<CoursePic> byId = coursePicRepository.findById(courseId);
        if (byId.isPresent()){
            coursePic = byId.get();
        }
        return coursePic;
    }

    //课程视图查询
    public CourseView courseView(String id) {
        CourseView courseView = new CourseView();
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()) courseView.setCourseBase(courseBaseOptional.get());
        Optional<CourseMarket>  courseMarketOptional = courseMarketRepository.findById(id);
        if(courseBaseOptional.isPresent()) courseView.setCourseMarket(courseMarketOptional.get());
        Optional<CoursePic> coursePicOption = coursePicRepository.findById(id);
        if (coursePicOption.isPresent()) courseView.setCoursePic(coursePicOption.get());
        TeachplanNode teachplanNode = teachplanMapper.findTeachplanById(id);
        if (teachplanNode!=null) courseView.setTeachplanNode(teachplanNode);
        return courseView;
    }

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;
    

    //预览课程
    public CoursePublishResult preview(String courseId) {
        CourseBase courseBase = findCourse(courseId);
        //发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId+".html");
        //页面别名
        cmsPage.setPageAliase(courseBase.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre+courseId);
        //远程请求cms保存页面信息
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if(!cmsPageResult.isSuccess()){
            return new CoursePublishResult(CommonCode.FAIL,null);
        }
        //页面id
        String pageId = cmsPageResult.getCmsPage().getPageId();
        //页面url
        String pageUrl = previewUrl+pageId;
        //更新课程发布状态
//        courseBaseMapper.updateSatus(courseId,"202002");
        return new CoursePublishResult(CommonCode.SUCCESS,pageUrl);
    }

    //判断课程信息是否完整
    public CourseBase findCourse(String courseId){
        //判断课程是否存在
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        CourseBase courseBase = null;
        if (!optional.isPresent()){
            ExecptionCast.cast(CourseCode.COURSE_NOTFIND);
        }else {
            courseBase = optional.get();
        }
        Optional<CourseMarket> courseMarket = courseMarketRepository.findById(courseId);
        if (!courseMarket.isPresent()) ExecptionCast.cast(CourseCode.COURSE_MARKETISNULL);
        try {
            TeachplanNode teachplanById = teachplanMapper.findTeachplanById(courseId);
            if (teachplanById==null) ExecptionCast.cast(CourseCode.TEACHPLAN_ISNULL);
        }catch (Exception e){
            ExecptionCast.cast(CourseCode.TEACHPLAN_ISNULL);
        }
        return courseBase;
    }


    //查看课程发布状态
    public CourseBase findCourseStatus(String id) {
        Optional<CourseBase> byId = courseBaseRepository.findById(id);
        if (byId.isPresent()) return byId.get();
        return null;
    }

    //一键发布课程
    public CmsPostPageResult publish(String courseId) {
        CourseBase courseBase = findCourse(courseId);
        //发布课程预览页面
        CmsPage cmsPage = new CmsPage();
        //站点
        cmsPage.setSiteId(publish_siteId);//课程预览站点
        //模板
        cmsPage.setTemplateId(publish_templateId);
        //页面名称
        cmsPage.setPageName(courseId+".html");
        //页面别名
        cmsPage.setPageAliase(courseBase.getName());
        //页面访问路径
        cmsPage.setPageWebPath(publish_page_webpath);
        //页面存储路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //数据url
        cmsPage.setDataUrl(publish_dataUrlPre+courseId);
        //远程请求cms保存页面信息
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        //向数据库保存课程索引信息
        CoursePub newCoursePub = saveCoursePub(courseId);
        if (newCoursePub.getTeachplan()==null) ExecptionCast.cast(CourseCode.TEACHPLAN_ISNULL);
        if (newCoursePub==null) {
            ExecptionCast.cast(CourseCode.COURSE_CREATECOURSEINDEXFAIL);
        }
        //向数据库中保存媒资管理
        saveTeachplanMediaPub(courseId);
        //更新课程发布状态
        if (cmsPostPageResult.isSuccess()) this.updateStatus(courseId);
        return cmsPostPageResult;
    }

    private CoursePub saveCoursePub(String courseId) {
        if (courseId==null) ExecptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        CoursePub coursePub = null;
        Optional<CoursePub> optional = coursePubRepository.findById(courseId);
        if (optional.isPresent()) {
            coursePub = optional.get();
        }else{
            coursePub = new CoursePub();
        }
        CoursePub coursePubNew = this.createCoursePub(courseId);
        BeanUtils.copyProperties(coursePubNew,coursePub);
        CoursePub save = coursePubRepository.save(coursePub);


        return save;
    }

    ////创建课程索引信息
    private CoursePub createCoursePub(String courseId) {
        CoursePub coursePub = new CoursePub();
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        //设置课程基本信息
        if (courseBaseOptional.isPresent()) {
            CourseBase courseBase = courseBaseOptional.get();
            BeanUtils.copyProperties(courseBase,coursePub);
        }else {
            ExecptionCast.cast(CourseCode.COURSE_NOTFIND);
        }
        //设置课程图片信息
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(courseId);
        if (coursePicOptional.isPresent()) {
            CoursePic coursePic = coursePicOptional.get();
            BeanUtils.copyProperties(coursePic,coursePub);
        }
        //设置课程详情信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(courseId);
        if (courseMarketOptional.isPresent()) {
            CourseMarket courseMarket = courseMarketOptional.get();
            BeanUtils.copyProperties(courseMarket,coursePub);
        }
        //设置课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.findTeachplanById(courseId);
        if (teachplanNode!=null) {
            String s = JSON.toJSONString(teachplanNode);
            coursePub.setTeachplan(s);
        }
        //设置时间戳
        coursePub.setTimestamp(new Date());
        //设置更新时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePub.setPubTime(date);
        return coursePub;
    }

    //更新课程发布状态
    public CourseBase updateStatus(String courseId){
        Optional<CourseBase> byId = courseBaseRepository.findById(courseId);
        CourseBase courseBase = null;
        if (byId.isPresent()){
            courseBase = byId.get();
        }
        //更新课程发布状态
        courseBase.setStatus("202002");
        CourseBase save = courseBaseRepository.save(courseBase);
        return save;
    }


    //保存媒资信息
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        if (teachplanMedia == null) ExecptionCast.cast(CourseCode.TEACHPLANMEDIA_ISNULL);
        String teachplanId = teachplanMedia.getTeachplanId();
        Teachplan teachplan = null;
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(teachplanId);
        if (teachplanOptional.isPresent()){
            teachplan = teachplanOptional.get();
        }else {
            ExecptionCast.cast(CourseCode.TEACHPLANMEDIA_ISNULL);
        }
        String grade = teachplan.getGrade();
        if (!"3".equals(grade)){
            ExecptionCast.cast(CourseCode.TEACHPLAN_MISMATCHING);
        }
        TeachplanMedia save = teachplanMediaRepository.save(teachplanMedia);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //保存课程计划媒资信息
    private void saveTeachplanMediaPub(String courseId){
        //查询所有媒质
        List<TeachplanMedia> teachplanMedias = teachplanMediaRepository.findByCourseId(courseId);
        if (teachplanMedias==null||teachplanMedias.size()<1)
            ExecptionCast.cast(CourseCode.COURSE_MEDIS_ISNULL);
        //先删除数据
        teachplanMediaPubRepository.deleteByCourseId(courseId);
        List<TeachplanMediaPub> teachplanMediaPubs = new ArrayList<>();
        for (TeachplanMedia teachplanMedia : teachplanMedias) {
            TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
            BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
            teachplanMediaPub.setTimestamp(new Date());
            teachplanMediaPubs.add(teachplanMediaPub);
        }
        teachplanMediaPubRepository.saveAll(teachplanMediaPubs);
    }

}
