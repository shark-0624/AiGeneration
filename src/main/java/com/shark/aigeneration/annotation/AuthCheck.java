package com.shark.aigeneration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 注解属性定义必须写成 类型 属性名() default 默认值;
     * 必须有某个角色
     */
    String mustRole() default "";
}
