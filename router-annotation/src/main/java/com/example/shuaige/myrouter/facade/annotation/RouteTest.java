package com.example.shuaige.myrouter.facade.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ShuaiGe on 2018-08-17.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RouteTest {
    /**
     * 路由地址
     */
    String path();

    /**
     * 分组
     * @return
     */
    String group() default "";

    /**
     * 路由名称，用来生成javadoc
     * @return
     */
    String name() default "";

    /**
     * 额外数据
     * @return
     */
    int extras() default Integer.MIN_VALUE;

    /**
     *
     * @return
     */
    int priority() default -1;
}
