package space.wangjiang.summer.form.validator;

import space.wangjiang.summer.form.Form;
import space.wangjiang.summer.form.Regex;
import space.wangjiang.summer.util.RegexUtil;

import java.lang.reflect.Field;

public class RegexValidator implements FormValidator<Regex> {

    @Override
    public boolean valid(Form form, Field field, Regex regex, String fileValue) {
        if (isRegexValid(regex, fileValue)) {
            return true;
        }
        form.setErrorMsg(regex.errorMsg());
        return false;
    }

    private boolean isRegexValid(Regex regex, String value) {
        return RegexUtil.isMatch(regex.regex(), value);
    }
}
