package com.xuecheng.auth.config;

import com.xuecheng.auth.service.UserJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
/**
 * 需要token 携带更多信息, 所以重写了DefaultUserAuthenticationConverter中的convertUserAuthentication 方法.
 *
 * public class DefaultUserAuthenticationConverter implements UserAuthenticationConverter {
 *
 *  默认只会把 username 加入token中
 * public Map<String, ?> convertUserAuthentication(Authentication authentication) {
 *         Map<String, Object> response = new LinkedHashMap();
 *         response.put("user_name", authentication.getName());
 *         if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
 *             response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
 *         }
 *
 *         return response;
 *     }
 *    }
 */

@Component
public class CustomUserAuthenticationConverter extends DefaultUserAuthenticationConverter {
    @Autowired
    UserDetailsService userDetailsService;

    /**
     *  定义token 的中的内容
     *  authentication 代表当前用户相关信息.
     *                          getPrincipal() 可以得到当前UserDetail对象
     *                          getAuthorities() 可以得到当前的权限对象
     * @return
     */
    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        LinkedHashMap response = new LinkedHashMap();
        String name = authentication.getName();
        response.put("user_name", name);

        Object principal = authentication.getPrincipal();
        UserJwt userJwt = null;
        if(principal instanceof  UserJwt){
            userJwt = (UserJwt) principal;
        }else{
            //refresh_token默认不去调用userdetailService获取用户信息，这里我们手动去调用，得到 UserJwt
            UserDetails userDetails = userDetailsService.loadUserByUsername(name);
            userJwt = (UserJwt) userDetails;
        }
        response.put("name", userJwt.getName());
        response.put("id", userJwt.getId());
        response.put("utype",userJwt.getUtype());
        response.put("userpic",userJwt.getUserpic());
        response.put("companyId",userJwt.getCompanyId());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }

        return response;
    }


}
