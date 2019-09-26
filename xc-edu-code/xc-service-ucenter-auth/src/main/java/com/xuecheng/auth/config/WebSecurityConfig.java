package com.xuecheng.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity// 相当于声明了一个过滤器
@Order(-1)//标记定义了组件的加载顺序 值越小拥有越高的优先级
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**HttpSecurity 与WebSecurity区别?
     *
     * 相同点: 2个都是定义需要安全控制的请求
     *
     * 区别: HttpSecurity是WebSecurity的一部分.
     *
     */

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/userlogin","/userlogout","/userjwt");

    }

//    //Http安全配置，对每个到达系统的http请求链接进行校验
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//
//        http.csrf().disable()
//                .httpBasic().and()
//                .formLogin()
//                .and()
//                .authorizeRequests().anyRequest().authenticated();
//
//    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }
    //采用bcrypt对密码进行编码
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic().and()
                .formLogin()
                .and()
                .authorizeRequests().anyRequest().authenticated();

    }
}
