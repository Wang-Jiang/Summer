package space.wangjiang.summer.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by WangJiang on 2017/9/9.
 * AOP注解，用于在方法调用前执行拦截器
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {

    Class<? extends Interceptor>[] value();

}
