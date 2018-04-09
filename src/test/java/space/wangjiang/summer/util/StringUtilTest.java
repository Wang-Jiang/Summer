package space.wangjiang.summer.util;

import org.junit.Test;

/**
 * Created by WangJiang on 2017/9/29.
 */
public class StringUtilTest {

    @Test
    public void lowerCamelCaseTest() {
        assert "strNameTest".equals(StringUtil.lowerCamelCase("Str_Name_test"));
        assert "strName".equals(StringUtil.lowerCamelCase("Str_name_"));
        assert "strNameTest".equals(StringUtil.lowerCamelCase("str_name__test"));
        assert "strNameTest".equals(StringUtil.lowerCamelCase("_str_name__test"));
    }

    @Test
    public void upperCamelCaseTest() {
        assert "StrNameTest".equals(StringUtil.upperCamelCase("Str_Name_test"));
        assert "StrName".equals(StringUtil.upperCamelCase("str_name_"));
        assert "StrNameTest".equals(StringUtil.upperCamelCase("str_name__test"));
        assert "StrNameTest".equals(StringUtil.upperCamelCase("_str_name__test"));
    }

    @Test
    public void isBlankTest() {
        assert StringUtil.isBlank("   ");
        assert StringUtil.isBlank("  ");
        assert !StringUtil.isBlank(" 1 ");
    }

    @Test
    public void isNumericTest() {
        assert StringUtil.isNumeric("1236");
        assert StringUtil.isNumeric("26");
        assert StringUtil.isNumeric("72");
        assert !StringUtil.isNumeric("+125");
        assert !StringUtil.isNumeric("1 266");
        assert !StringUtil.isNumeric("36.3");
        assert !StringUtil.isNumeric("1 ");
    }

    @Test
    public void isAllEmptyTest() {
        assert StringUtil.isAllEmpty((CharSequence) null);
        assert StringUtil.isAllEmpty(null, "", "");
        assert !StringUtil.isAllEmpty("", "", "  ");
        assert !StringUtil.isAllEmpty(null, "", "XXX");
    }

}
