package space.wangjiang.summer.form;

import space.wangjiang.summer.form.validator.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * 表单校验器配置
 */
public class FormValidatorMapping {

    private static final Map<Class, FormValidator> mapping = new HashMap<>();

    static {
        // 初始自动配置Summer内置的注解
        register(Required.class, new RequiredValidator());
        register(Type.class, new TypeValidator());
        register(In.class, new InValidator());
        register(Length.class, new LengthValidator());
        register(Regex.class, new RegexValidator());
    }

    /**
     * 注册注解和对应的校验器
     */
    public static <T extends Annotation> void register(Class<T> annotation, FormValidator<T> validator) {
        mapping.put(annotation, validator);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation, V extends FormValidator<A>> V getValidator(Class<A>  annotation) {
        return (V) mapping.get(annotation);
    }

}
