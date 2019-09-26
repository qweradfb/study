package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExecptionCast;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class CmsConfigServcie {

    @Autowired
    private CmsConfigRepository cmsConfigRepository;


    //获取页面模型
    public CmsConfig getModelById(String id) {

        Optional<CmsConfig> optional = cmsConfigRepository.findById(id);
        if(optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }else{
            ExecptionCast.cast(CmsCode.CMS_PAGEMODEL_NOTEXITES);
        }
        return null;
    }
}
