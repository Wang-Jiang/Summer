package space.wangjiang.summer.form.validator;

import space.wangjiang.summer.form.Form;
import space.wangjiang.summer.form.In;

import java.lang.reflect.Field;

public class InValidator implements FormValidator<In> {

    @Override
    public boolean valid(Form form, Field field, In in, String fileValue) {
        if (isInValid(in, fileValue)) {
            return true;
        }
        form.setErrorMsg(in.errorMsg());
        return false;
    }

    private boolean isInValid(In in, Object value) {
        String[] array = in.in().split(",");
        for (String item : array) {
            if (item.equals(value)) return true;
        }
        return false;
    }
}
