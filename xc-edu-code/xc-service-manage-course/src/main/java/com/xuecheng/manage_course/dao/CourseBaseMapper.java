package com.xuecheng.manage_course.dao;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.course.CourseBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CourseBaseMapper {

    @Select("select * from course_base where company_id = #{companyId}")
    Page<CourseBase> findAllCourse(String companyId);
    @Update("update course_base set status = #{status} where id = #{id}")
    long updateSatus(@Param("id") String courseId, @Param("status") String satus);
}
