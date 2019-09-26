package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

public class ExecptionCast {
    public static void cast(ResultCode resultCode){
        throw new CustomExecption(resultCode);
    }
}
