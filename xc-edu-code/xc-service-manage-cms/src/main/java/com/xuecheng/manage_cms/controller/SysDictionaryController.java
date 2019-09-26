package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.dictionary.SysDictionaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys")
public class SysDictionaryController implements SysDictionaryControllerApi {

    @Autowired
    private SysDictionaryService sysDictionaryService;

    @GetMapping("/dictionary/get/{type}")
    public SysDictionary getDictionary(@PathVariable("type") String type) {
        SysDictionary sysDictionary = sysDictionaryService.getDictionary(type);
        return sysDictionary;
    }
}
