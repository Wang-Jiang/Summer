package space.wangjiang.summer.util;

import org.junit.Test;

/**
 * Created by WangJiang on 2017/10/2.
 */
public class RegexUtilTest {

    @Test
    public void isEmailTest() {
        assert RegexUtil.isEmail("123@example.com");
        assert RegexUtil.isEmail("abc@gmail.com");
        assert RegexUtil.isEmail("abcd@126.com");
        assert !RegexUtil.isEmail("abcd@126.网站");
        assert !RegexUtil.isEmail("abc@test");
        assert !RegexUtil.isEmail("aa@//a.com");
    }

    @Test
    public void isURLTest() {
        assert RegexUtil.isURL("https://123.com");
        assert RegexUtil.isURL("http://123.com/");
        assert RegexUtil.isURL("ftp://192.168.2.1");
        assert RegexUtil.isURL("ftp://192.168.2.1:8080/");
        assert !RegexUtil.isURL("ftp:aa//as.com");
        assert RegexUtil.isURL("ftp:///a");
    }

    @Test
    public void isIpTest() {
        assert RegexUtil.isIP("120.0.20.12");
        assert RegexUtil.isIP("192.168.20.12");
        assert !RegexUtil.isIP("266.168.20.12");
        assert !RegexUtil.isIP("10.20.12");
        assert !RegexUtil.isIP("120.0.20.");
    }

    @Test
    public void isMobileSimpleTest() {
        assert RegexUtil.isMobileSimple("18250018520");
        assert RegexUtil.isMobileSimple("18175071290");
        assert !RegexUtil.isMobileSimple("110");
        assert !RegexUtil.isMobileSimple("11000511100");
        assert !RegexUtil.isMobileSimple("12345678902455555");
    }

    @Test
    public void isMobileExactTest() {
        assert RegexUtil.isMobileExact("18250018520");
        assert RegexUtil.isMobileExact("18175071290");
        assert !RegexUtil.isMobileExact("110");
        assert !RegexUtil.isMobileExact("11000511100");
        assert !RegexUtil.isMobileExact("12345678902455555");
    }

}
