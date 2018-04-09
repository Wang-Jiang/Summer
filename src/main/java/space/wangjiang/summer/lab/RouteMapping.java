package space.wangjiang.summer.lab;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于在Controller中直接配置路由，不需要在SummerConfig中配置initRoute方法
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RouteMapping {

    String url();
    String viewPath() default "";

}
