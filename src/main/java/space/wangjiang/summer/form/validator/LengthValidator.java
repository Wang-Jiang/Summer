package space.wangjiang.summer.form.validator;


import space.wangjiang.summer.form.Form;
import space.wangjiang.summer.form.Length;

import java.lang.reflect.Field;

public class LengthValidator implements FormValidator<Length> {

    @Override
    public boolean valid(Form form, Field field, Length length, String fileValue) {
        if (isLengthValid(length, fileValue)) {
            return true;
        }
        form.setErrorMsg(length.errorMsg());
        return false;
    }

    private boolean isLengthValid(Length length, String value) {
        int minLength = length.min();
        int maxLength = length.max();
        return value.length() >= minLength && value.length() <= maxLength;
    }

}
