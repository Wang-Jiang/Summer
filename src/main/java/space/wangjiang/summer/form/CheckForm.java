package space.wangjiang.summer.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by WangJiang on 2017/9/10.
 * 这个注解是简化Controller的getForm()的功能
 * 不需要写form.isValid()
 * 直接在方法加上@CheckForm(Form.class)就可以了
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckForm {

    Class<? extends Form> value();

}
