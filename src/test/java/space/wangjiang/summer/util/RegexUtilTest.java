package space.wangjiang.summer.util;

import org.junit.Test;

import java.util.regex.Pattern;

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

    /**
     * 有缺陷的正则在匹配字符串的时候会非常消耗资源，例如下面的正则表达式会陷入死循环
     */
    @Test
    public void reDosTest() {
        String email = "asetryinvdrgeeaedcfdsda";
        String e = "^(\\w+((-\\w+)|(.\\w+))*)+\\w+((-\\w+)|(.\\w+))*\\@[A-Za-z0-9]+((.|-)[A-Za-z0-9]+)*.[A-Za-z0-9]+$";
        boolean b = Pattern.matches(e, email);
        System.out.println(b);
    }

}
