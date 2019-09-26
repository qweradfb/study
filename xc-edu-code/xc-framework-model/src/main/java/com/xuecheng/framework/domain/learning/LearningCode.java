package com.xuecheng.framework.domain.learning;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.ResultCode;

public enum  LearningCode implements ResultCode {
    LEARNING_GETMEDIA_ERROR(false,28001,"获取媒体支援错误"),
    CHOOSECOURSE_USERISNULL(false,28002,"用户id为空"),
    CHOOSECOURSE_TASKISNULL(false,28003,"消息为空");


    boolean success;

    int code;
    String message;
    private LearningCode(boolean success,int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }
    private static final ImmutableMap<Integer, LearningCode> CACHE;

    static {
        final ImmutableMap.Builder<Integer, LearningCode> builder = ImmutableMap.builder();
        for (LearningCode commonCode : values()) {
            builder.put(commonCode.code(), commonCode);
        }
        CACHE = builder.build();
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}

