package space.wangjiang.summer.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by WangJiang on 2017/9/30.
 * 移除拦截器的注解，支持类和方法
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Remove {

    Class<? extends Interceptor>[] value() default {};

}
