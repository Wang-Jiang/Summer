package space.wangjiang.summer.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Type {

    enum TYPE {TEXT, NUMBER, TEL, EMAIL, URL}

    Type.TYPE type() default Type.TYPE.TEXT;

    String errorMsg() default "";

}
