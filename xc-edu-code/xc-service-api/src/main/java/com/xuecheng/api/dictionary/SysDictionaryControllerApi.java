package com.xuecheng.api.dictionary;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.framework.domain.system.SysDictionaryValue;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api(value="系统字典")
public interface SysDictionaryControllerApi {
    @ApiOperation("根据类型查系统字典")
     SysDictionary getDictionary(String code);
}
