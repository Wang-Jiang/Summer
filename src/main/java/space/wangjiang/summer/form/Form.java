package space.wangjiang.summer.form;

import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.form.validator.FormValidator;
import space.wangjiang.summer.util.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by WangJiang on 2017/9/9.
 * 表单，用于校验提交的数据是否合法
 * 表单字段是指非静态和非final的字段，包括Form类定义的字段(私有、公开等)和从父类继承的公开字段
 * 表单的字段不支持非String类型的字段，如果需要int、double等，可以在getter方法处理
 * 非String类型的字段会增加验证的复杂度，因此直接禁止非String类型字段
 */
public abstract class Form {

    /**
     * 这个是@CheckForm注解放到Attribute中的Form的name
     * 为了防止覆盖Attribute中同名的值，选了一个长长的名字
     */
    public static final String ATTRIBUTE_FORM_NAME = "this_form_is_set_by_check_form_annotation";

    private String errorMsg = null;

    /**
     * 获取当前类声明的字段，和父类的public字段
     * 不获取静态和final字段
     */
    public Set<Field> getAllFormFields() {
        Set<Field> fields = new LinkedHashSet<>();
        for (Field field : getClass().getDeclaredFields()) {
            if (isValidFormField(field)) {
                fields.add(field);
            }
        }
        for (Field field : getClass().getFields()) {
            if (isValidFormField(field)) {
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * 是否是有效的表单字段
     */
    private boolean isValidFormField(Field field) {
        return !ReflectUtil.isStatic(field) && !ReflectUtil.isFinal(field);
    }

    /**
     * 好吧，依旧是反射实现的
     */
    @SuppressWarnings("unchecked")
    public boolean isValid() {
        Set<Field> fields = getAllFormFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String value;
            try {
                value = (String) field.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                errorMsg = e.getMessage();
                return false;
            }
            Required required = field.getAnnotation(Required.class);
            if (required == null && value == null) {
                //该字段不是必填并且为null，跳过该字段检查
                continue;
            }
            if (!FormValidatorMapping.getValidator(Required.class).valid(this, field, required, value)) {
                return false;
            }

            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                // 排除掉 Required
                if (annotation instanceof Required) {
                    continue;
                }
                // 找到对应的字段校验器，getClass()返回的是代理类 com.sun.proxy.$Proxy12
                Class<? extends Annotation> clazz = annotation.annotationType();
                FormValidator validator = FormValidatorMapping.getValidator(clazz);
                if (validator != null) {
                    if (!validator.valid(this, field, annotation, value)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isNotValid() {
        return !isValid();
    }

    /**
     * 不能传入HttpServletRequest，因为当请求中包含文件的时候，是没有办法获取到字段值的
     * 只能通过Controller的getPara()才能正确获得值，因为它内部是UploadRequestWrapper能正确获取值
     */
    public static <T extends Form> T getNewForm(Controller controller, Class<T> formClass) {
        try {
            T form = formClass.newInstance();
            Set<Field> fields = form.getAllFormFields();
            for (Field field : fields) {
                field.setAccessible(true);
                field.set(form, controller.getPara(field.getName()));
            }
            return form;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置错误消息
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 当有字段不符合要求的时候，返回错误的信息
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 当使用@CheckForm注解，并且校验失败的时候调用
     * 自定义Form可以重写改方法，自定义输出的格式，或者跳转、渲染页面等等
     */
    public void renderError(Controller controller, String errorMsg) {
        controller.renderText(errorMsg);
    }

}
