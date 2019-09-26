package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes!=null) {
                HttpServletRequest request = attributes.getRequest();
                String authorization = request.getHeader("authorization");
                if (authorization!=null&&!authorization.equals("")){
                    requestTemplate.header("authorization",authorization);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
