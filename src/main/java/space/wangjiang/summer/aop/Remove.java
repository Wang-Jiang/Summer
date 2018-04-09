package space.wangjiang.summer.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by WangJiang on 2017/9/30.
 * 移除拦截器的注解
 * 因为Summer暂时不准备支持全局拦截器，所以remove只需要配置到method
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Remove {

    Class<? extends Interceptor>[] value() default {};

}
