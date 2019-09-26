package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.sun.xml.internal.messaging.saaj.util.Base64;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExecptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.utils.CookieUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Value("${auth.cookieDomain}")
    String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    //登录验证
    public LoginResult login(LoginRequest loginRequest,HttpServletResponse response) {
        AuthToken authToken = this.applyToken(loginRequest, response);
        if(authToken == null){
            ExecptionCast.cast(AuthCode.AUTH_GETTOKEN_ERROR);
        }
        //将 token存储到redis
        String access_token = authToken.getAccess_token();
        String content = JSON.toJSONString(authToken);
        boolean saveTokenResult = saveToken(access_token, content, tokenValiditySeconds);
        if(!saveTokenResult){
            ExecptionCast.cast(AuthCode.AUTH_GETTOKEN_ERROR);
        }
        return new LoginResult(CommonCode.SUCCESS,access_token);
    }

    //存储令牌到redis
    private boolean saveToken(String access_token,String content,long ttl){
        //令牌名称
        String name = "user_token:" + access_token;
        //保存到令牌到redis
        stringRedisTemplate.boundValueOps(name).set(content,ttl, TimeUnit.SECONDS);
        //获取过期时间
        Long expire = stringRedisTemplate.getExpire(name);
        return expire>0;
    }
    //从redis中获取令牌
    public AuthToken getUserToken(String access_token){
        //令牌名称
        String userToken = "user_token:" + access_token;
        String userTokenString = stringRedisTemplate.opsForValue().get(userToken);
        if (StringUtils.isNotEmpty(userTokenString)){
            try {
                AuthToken authToken = JSON.parseObject(userTokenString, AuthToken.class);
                return authToken;
            }catch (Exception e){
                LOGGER.error(e.getMessage());
            }
        }
        return null;
    }
    //从redis中删除令牌
    public boolean delToken(String access_token){
        String name = "user_token:" + access_token;
        stringRedisTemplate.delete(name);
        return true;
    }

    private AuthToken applyToken(LoginRequest loginRequest,HttpServletResponse response){
        //校验账号是否输入
        if(loginRequest == null || StringUtils.isEmpty(loginRequest.getUsername())){
            ExecptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }
        //校验密码是否输入
        if(StringUtils.isEmpty(loginRequest.getPassword())){
            ExecptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        ServiceInstance choose = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        URI uri = choose.getUri();
        String authUrl = uri+"/auth/oauth/token";
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        String httpbasic = httpbasic(clientId, clientSecret);
        headers.add("Authorization", httpbasic);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type","password");
        body.add("username",loginRequest.getUsername());
        body.add("password",loginRequest.getPassword());
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new
                HttpEntity<>(body, headers);
        Map result = null;
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
            //当响应的值为400或401时候也要正常响应，不要抛出异常
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        try {
            //远程调用申请令牌
            ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST,
                    multiValueMapHttpEntity, Map.class);
            result = exchange.getBody();
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            ExecptionCast.cast(AuthCode.AUTH_GETTOKEN_ERROR);
        }
        if (result==null) ExecptionCast.cast(AuthCode.AUTH_GETTOKEN_ERROR);
        String jwt_token = (String) result.get("access_token");
        String refresh_token = (String) result.get("refresh_token");
        String access_token = (String) result.get("jti");
        if (jwt_token==null||refresh_token==null||access_token==null){
            String error_description = (String) result.get("error_description");
            if(error_description.contains("UserDetailsService returned null, which is an interface contract violation")){
                ExecptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
            }
            if (error_description.contains("用户名或密码错误")){
                ExecptionCast.cast(AuthCode.AUTH_USERNAMEORPAW_ERROR);
            }

            ExecptionCast.cast(AuthCode.AUTH_GETTOKEN_ERROR);
        }
        AuthToken authToken = new AuthToken();
        authToken.setAccess_token(access_token);
        authToken.setJwt_token(jwt_token);
        authToken.setRefresh_token(refresh_token);
        //将令牌保存到cookie
        this.saveCookie(access_token,response);
        return authToken;
    }

    //将令牌保存到cookie
    private void saveCookie(String token,HttpServletResponse response){
    //添加cookie 认证令牌，最后一个参数设置为false，表示允许浏览器获取
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, cookieMaxAge, false);
    }

    //清除cookie
    public void clearCookie(String token,HttpServletResponse response){
        CookieUtil.addCookie(response, cookieDomain, "/", "uid", token, 0, false);
    }


    private String httpbasic(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64.encode(string.getBytes());
        return "Basic "+new String(encode);
    }

}
