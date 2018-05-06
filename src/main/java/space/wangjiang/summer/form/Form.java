package space.wangjiang.summer.form;

import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.util.ReflectUtil;
import space.wangjiang.summer.util.RegexUtil;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by WangJiang on 2017/9/9.
 * 这个是用于表单验证
 * TODO 直接禁止在Form中除String以外的类型，包括int、File等，分情况处理非String类型的字段会增加验证的复杂度
 */
public abstract class Form {

    /**
     * 这个是@CheckForm注解放到Attribute中的Form的name
     * 为了防止覆盖Attribute中同名的值，选了一个长长的名字
     */
    public static final String ATTRIBUTE_FORM_NAME = "this_form_is_set_by_check_form_annotation";

    private String errorMsg = null;

    /**
     * 获取当前类声明的属性，和父类的public属性
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
            Object object;
            try {
                object = field.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                errorMsg = e.getMessage();
                return false;
            }
            Required required = field.getAnnotation(Required.class);
            //如果不是required并且object == null，直接返回true
            if (required == null && object == null) {
                continue; //该字段不是必填并且为null，跳过该字段检查
            }
            if (!isRequiredValid(required, object)) {
                errorMsg = required.errorMsg();
                return false;
            }

            //必须先校验required，因为如果不是必填的，当其值为空的时候就不需要检查了
            Type type = field.getAnnotation(Type.class);
            if (!isTypeValid(type, object)) {
                errorMsg = type.errorMsg();
                return false;
            }

            //长度判断
            Length length = field.getAnnotation(Length.class);
            if (!isLengthValid(length, object)) {
                errorMsg = length.errorMsg();
                return false;
            }

            //In判断
            In in = field.getAnnotation(In.class);
            if (!isInValid(in, object)) {
                errorMsg = in.errorMsg();
                return false;
            }

            //正则校验
            Regex regex = field.getAnnotation(Regex.class);
            if (!isRegexValid(regex, object)) {
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
    private boolean isRequiredValid(Required required, Object value) {
        if (required == null) {
            return true;
        }
        //不为null
        return value != null;
    }

    private boolean isTypeValid(Type type, Object value) {
        if (type == null) {
            return true;
        }
        String strValue = String.valueOf(value);
        switch (type.type()) {
            case TEXT:
                //TEXT没什么好判断的，直接返回true
                return true;
            case EMAIL:
                return RegexUtil.isEmail(strValue);
            case NUMBER:
                return RegexUtil.isNumber(strValue);
            case TEL:
                return RegexUtil.isTel(strValue);
            case URL:
                return RegexUtil.isURL(strValue);
        }
        return true;
    }

    private boolean isLengthValid(Length length, Object value) {
        if (length == null) {
            return true;
        }
        String strValue = String.valueOf(value);
        int minLength = length.min();
        int maxLength = length.max();
        return strValue.length() >= minLength && strValue.length() <= maxLength;
    }

    private boolean isInValid(In in, Object value) {
        if (in == null) {
            return true;
        }
        String[] array = in.in().split(",");
        String strValue = String.valueOf(value);
        for (String item : array) {
            if (item.equals(strValue)) return true;
        }
        return false;
    }

    private boolean isRegexValid(Regex regex, Object value) {
        if (regex == null) {
            return true;
        }
        return RegexUtil.isMatch(regex.regex(), String.valueOf(value));
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
                //赋值
                setFieldValue(form, field, controller.getPara(field.getName()));
            }
            return form;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断Field的类型赋值
     * TODO 准备让Form不再支持非String类型的
     */
    private static void setFieldValue(Object object, Field field, String value) throws IllegalAccessException {
        if (value == null || value.length() == 0) {
            return;
        }
        Class type = field.getType();
        if (type == Byte.TYPE) {
            field.setByte(object, Byte.parseByte(value));
        } else if (type == Character.TYPE) {
            field.setChar(object, value.charAt(0));
        } else if (type == Short.TYPE) {
            field.setShort(object, Short.parseShort(value));
        } else if (type == Integer.TYPE) {
            field.setInt(object, Integer.parseInt(value));
        } else if (type == Long.TYPE) {
            field.setLong(object, Long.parseLong(value));
        } else if (type == Float.TYPE) {
            field.setFloat(object, Float.parseFloat(value));
        } else if (type == Double.TYPE) {
            field.setDouble(object, Double.parseDouble(value));
        } else if (type == Boolean.TYPE) {
            field.setBoolean(object, Boolean.parseBoolean(value));
        } else {
            field.set(object, value);
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
