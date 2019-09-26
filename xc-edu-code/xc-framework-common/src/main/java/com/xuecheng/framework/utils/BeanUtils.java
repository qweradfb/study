package com.xuecheng.framework.utils;

import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;
import java.util.Objects;

public class BeanUtils {
    public static <T,S> S setPropertys(T t,S s){
        Objects.requireNonNull(t);
        Objects.requireNonNull(s);
        Method[] methods = t.getClass().getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("get")){
//                method
            }
        }
        return s;
    }

    public static void main(String[] args) {
        BeanUtils.setPropertys(new User(),new User());
    }

}
