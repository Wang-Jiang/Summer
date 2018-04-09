package space.wangjiang.summer.route;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by WangJiang on 2017/9/12.
 * NotRoute注解，用于在Controller标记非Route的方法
 * 关于Route，在Controller中被当做是Route的是参数为空的无返回值非静态的公开方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotRoute {

}
