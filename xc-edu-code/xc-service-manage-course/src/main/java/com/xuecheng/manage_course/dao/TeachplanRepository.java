package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeachplanRepository extends JpaRepository<Teachplan,String> {
    Teachplan findByCourseidAndParentid(String courseId,String parentId);
    void deleteByCourseid(String id);

}
