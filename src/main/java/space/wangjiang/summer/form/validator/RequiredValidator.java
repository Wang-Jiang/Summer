package space.wangjiang.summer.form.validator;

import space.wangjiang.summer.form.Form;
import space.wangjiang.summer.form.Required;

import java.lang.reflect.Field;

/**
 * 必填校验器
 */
public class RequiredValidator implements FormValidator<Required> {

    @Override
    public boolean valid(Form form, Field field, Required required, String fileValue) {
        if (isRequiredValid(required, fileValue)) {
            return true;
        }
        form.setErrorMsg(required.errorMsg());
        return false;
    }

    private boolean isRequiredValid(Required required, String value) {
        // Required是比较特殊的，它可能会传入null
        if (required == null) {
            return true;
        }
        //不为null
        return value != null;
    }

}
