package space.wangjiang.summer.form.validator;

import space.wangjiang.summer.form.Form;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 表单字段处理器
 */
public interface FormValidator<T extends Annotation> {

    /**
     * 校验字段
     *
     * @param form       表单实例
     * @param field      待校验的字段
     * @param annotation 注解
     * @param fileValue  字段的值
     */
    boolean valid(Form form, Field field, T annotation, String fileValue);

}
