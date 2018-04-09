package space.wangjiang.summer.route;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WangJiang on 2017/9/10.
 */
public class UrlMappingTest {

    public static List<String> urls = new ArrayList<>();

    static {
        urls.add("/user/{userId}");
        urls.add("/user/{userId}/blog/{blogId}");
        urls.add("/user/{userId}/blog/{blogId}/view");
        urls.add("/blog/{blogId}");
        urls.add("/blog/{blogId}/edit");
        urls.add("/blog/{blogId}/delete");
    }

    public static void main(String[] args) {
        //打印出参数列表
        for (String url : urls) {
            Pattern pattern = Pattern.compile("\\{\\w+}");
            Matcher matcher = pattern.matcher(url);
            while (matcher.find()) {
                System.out.print(matcher.group(0) + "  ");//{userId}  {blogId}
            }
            System.out.println();
        }

        System.out.println(getUrl("/user/36"));
        System.out.println(getUrl("/user/36/blog/72"));
        System.out.println(getUrl("/user/36/blog/72/view"));
        System.out.println(getUrl("/blog/7"));
    }

    public static String getUrl(String target) {
        for (String url : urls) {
            //使用正则
            String regexUrl = url.replaceAll("\\{\\w+}", "(\\\\w+)");
            System.out.println(regexUrl);
            if (Pattern.matches(regexUrl, target)) {
                //匹配
                Matcher matcher = Pattern.compile(regexUrl).matcher(target);
                if (matcher.find()) {
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        System.out.print(matcher.group(i) + "---");
                    }
                }
                System.out.println();
                return url;
            }
        }
        return null;
    }
}
