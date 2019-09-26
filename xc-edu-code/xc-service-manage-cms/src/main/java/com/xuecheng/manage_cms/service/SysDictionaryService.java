package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysDictionaryService {

    @Autowired
    private SysDictionaryRepository sysDictionaryRepository;

    public SysDictionary getDictionary(String type) {
        SysDictionary byDType = sysDictionaryRepository.findByDType(type);
        return byDType;
    }
}
