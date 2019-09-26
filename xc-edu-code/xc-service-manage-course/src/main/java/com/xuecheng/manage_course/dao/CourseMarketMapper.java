package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.CourseMarket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import javax.persistence.Column;
import java.util.Date;

@Mapper
@Repository
public interface CourseMarketMapper {

    @Insert("insert into course_market values(#{id},#{charge},#{valid},#{expires},#{qq},#{price},#{price_old},#{startTime},#{endTime})")
    void addCourseMarket(CourseMarket courseMarket);
}
