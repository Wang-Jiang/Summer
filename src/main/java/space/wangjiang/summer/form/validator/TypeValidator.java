package space.wangjiang.summer.form.validator;

import space.wangjiang.summer.form.Form;
import space.wangjiang.summer.form.Type;
import space.wangjiang.summer.util.RegexUtil;

import java.lang.reflect.Field;

/**
 * 处理字段类型
 */
public class TypeValidator implements FormValidator<Type> {

    @Override
    public boolean valid(Form form, Field field, Type type, String fileValue) {
        if (isTypeValid(type, fileValue)) {
            return true;
        }
        form.setErrorMsg(type.errorMsg());
        return false;
    }

    private boolean isTypeValid(Type type, String value) {
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

}
