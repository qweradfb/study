package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TeachplanMapper {
    TeachplanNode findTeachplanById(String id);
    @Delete("delete from teachplan where id = #{id} or parentid = #{id}")
    void deleteTeachplan(String id);
}
