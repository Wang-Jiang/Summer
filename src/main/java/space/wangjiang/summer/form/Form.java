package space.wangjiang.summer.form;

import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.util.ReflectUtil;
import space.wangjiang.summer.util.RegexUtil;

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
                continue; //该字段不是必填并且为null，跳过该字段检查
            }
            if (!isRequiredValid(required, value)) {
                errorMsg = required.errorMsg();
                return false;
            }

            //必须先校验required，因为如果不是必填的，当其值为空的时候就不需要检查了
            Type type = field.getAnnotation(Type.class);
            if (!isTypeValid(type, value)) {
                errorMsg = type.errorMsg();
                return false;
            }

            //长度判断
            Length length = field.getAnnotation(Length.class);
            if (!isLengthValid(length, value)) {
                errorMsg = length.errorMsg();
                return false;
            }

            //In判断
            In in = field.getAnnotation(In.class);
            if (!isInValid(in, value)) {
                errorMsg = in.errorMsg();
                return false;
            }

            //正则校验
            Regex regex = field.getAnnotation(Regex.class);
            if (!isRegexValid(regex, value)) {
                errorMsg = regex.errorMsg();
                return false;
            }
        }
        return true;
    }

    public boolean isNotValid() {
        return !isValid();
    }

    //各种判断

    /**
     * 是否是必填
     */
    private boolean isRequiredValid(Required required, String value) {
        if (required == null) {
            return true;
        }
        //不为null
        return value != null;
    }

    private boolean isTypeValid(Type type, String value) {
        if (type == null) {
            return true;
        }
        switch (type.type()) {
            case TEXT:
                //TEXT没什么好判断的，直接返回true
                return true;
            case EMAIL:
                return RegexUtil.isEmail(value);
            case NUMBER:
                return RegexUtil.isNumber(value);
            case TEL:
                return RegexUtil.isTel(value);
            case URL:
                return RegexUtil.isURL(value);
        }
        return true;
    }

    private boolean isLengthValid(Length length, String value) {
        if (length == null) {
            return true;
        }
        int minLength = length.min();
        int maxLength = length.max();
        return value.length() >= minLength && value.length() <= maxLength;
    }

    private boolean isInValid(In in, Object value) {
        if (in == null) {
            return true;
        }
        String[] array = in.in().split(",");
        for (String item : array) {
            if (item.equals(value)) return true;
        }
        return false;
    }

    private boolean isRegexValid(Regex regex, String value) {
        if (regex == null) {
            return true;
        }
        return RegexUtil.isMatch(regex.regex(), value);
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

    //当有字段不符合要求的时候，返回错误的信息
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
