package com.shark.aigeneration.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
/*
`implements WebMvcConfigurer`
SpringMVC 提供的扩展接口，可以重写里面方法，自定义 Web 的相关配置（跨域、拦截器、静态资源等）
 */

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //`CorsRegistry`是跨域配置注册器。
        //`addMapping("/**")`表示对所有路径进行跨域配置。
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                //允许前端请求头里面携带任意自定义头信息
                //比如 token、authorization 放在请求头里面传参，必须开启这个配置
                .allowedHeaders("*")
                //**暴露响应头**
                //默认情况下，浏览器前端 js 只能读取到一部分标准响应头。
                //如果你后端返回了自定义响应头（比如 token），前端 js 拿不到。
                //`exposedHeaders("*")`代表把后端所有响应头都对外开放给前端读取。
                .exposedHeaders("*");
    }












}
