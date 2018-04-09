package space.wangjiang.summer.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示值需要在某几个类型之中，例如性别只能是"男"或者"女"
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface In {

    /**
     * in使用字符串，逗号分割
     * In(in = "男,女", errorMsg = "输入有效的性别")
     */
    String in();

    String errorMsg() default "";

}
