package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class ExecptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecptionCatch.class);
    //使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> IMMUTABLE_MAP;
    //使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder =
            ImmutableMap.builder();

    static {
        builder.put(HttpMessageNotReadableException.class,CommonCode.REQUEST_PARAM_FALI);
        builder.put(MaxUploadSizeExceededException.class,CommonCode.UPLOAD_FILE_OUTOFSIZE);
    }

    @ExceptionHandler(CustomExecption.class)
    @ResponseBody
    public ResponseResult customExecption(CustomExecption e){
        LOGGER.error("catch exception : {}\r\nexception: ",e.getMessage(), e);
        ResultCode resultCode = e.getResultCode();
        ResponseResult responseResult = new ResponseResult(resultCode);
        return responseResult;
    }
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseResult systemExecption(Throwable e){
        e.printStackTrace();
        if (IMMUTABLE_MAP==null) IMMUTABLE_MAP = builder.build();
        System.out.println(IMMUTABLE_MAP);
        ResultCode resultCode = IMMUTABLE_MAP.get(e.getClass());
        ResponseResult responseResult;
        if (resultCode!=null){
            responseResult = new ResponseResult(resultCode);
        }else{
            responseResult = new ResponseResult(CommonCode.SERVER_ERROR);
        }
        return responseResult;
    }


}
