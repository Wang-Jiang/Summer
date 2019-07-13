package space.wangjiang.summer.route;

import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.config.SummerConfig;
import space.wangjiang.summer.controller.ControllerKit;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WangJiang on 2017/9/10.
 * <pre>
 * 全局的路由表
 *
 * 路由分为两类
 * 一类是精准路由表 /user/view
 * 另一类是参数路由表 /user/{userId}
 *
 * getRoute()方法先在精准路由表里查找，没有找到的话，找参数路由表
 * </pre>
 */
public class RouteConfig {

    /**
     * <pre>
     * 精准路由表 ExactRoute
     * url-->route的一个映射，route中包含实现这个url的controller和具体的method
     * 这个是精准匹配的，/user/36/这种不能存储在这里
     * </pre>
     */
    private Map<String, Route> exactRoutes = new HashMap<>();

    /**
     * 参数(正则)路由表，/user/{userId}
     * key是处理后的正则表达式
     * /user/(\w+)/blog/(\w+)，匹配/user/{userId}/blog/{blogId}
     */
    private Map<String, Route> regexRoutes = new HashMap<>();


    /**
     * <pre>
     * 添加一个路由
     *
     * 注意存在如下默认规则
     * /user  == /user/index
     * /user/ == /user/index
     *
     * 不过需要注意，如果在index方法上面加上@UrlMapping(url="/xxx")
     * 上面的默认规则将不会存在
     *
     * 如果 / -- > IndexController 而IndexController有一个user方法，就会出错
     * </pre>
     */
    public void addRoute(String url, Class<? extends Controller> clazz, String baseViewPath) {
        List<Method> methods = ControllerKit.getRouteMethod(clazz);
        for (Method method : methods) {
            //如果Method中有UrlMapping注解，有则优先使用里面的url
            UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);
            if (urlMapping != null) {
                addRoute(urlMapping.url(), clazz, method, baseViewPath);
                continue;
            }
            if (url.endsWith("/")) {
                addRoute(url + method.getName(), clazz, method, baseViewPath);
            } else {
                addRoute(url + "/" + method.getName(), clazz, method, baseViewPath);
            }
            // /user和/user/这种请求默认交给UserController.index()
            if (method.getName().equals("index")) {
                addRoute(url, clazz, method, baseViewPath);
                if (!url.endsWith("/")) {
                    addRoute(url + "/", clazz, method, baseViewPath);
                }
            }
        }
    }

    public void addRoute(String url, Class<? extends Controller> clazz) {
        addRoute(url, clazz, null);
    }

    /**
     * URL --> method
     */
    private void addRoute(String url, Class<? extends Controller> clazz, Method method, String viewPath) {
        //路由是否存在
        if (exactRoutes.get(url) != null || regexRoutes.get(url) != null) {
            EasyLogger.error("已经有这个路由 " + url + " --> " + clazz.getName() + "." + method.getName());
            throw new RuntimeException("已经有这个路由" + url);
        }
        //判断url是否包含路径参数，有则加到paramRoutes里面
        if (hasPathParam(url)) {
            String regexUrl = getRegexUrl(url);
            regexRoutes.put(regexUrl, new Route(url, regexUrl, clazz, method, viewPath));
        } else {
            exactRoutes.put(url, new Route(url, clazz, method, viewPath));
        }
        //是否打印路由
        if (SummerConfig.config.getConstantConfig().isShowRoutes()) {
            //整齐地打印路由表
            String indent = indent(34 - url.length());
            Logger.debug("添加路由:" + url + indent + clazz.getSimpleName() + "." + method.getName());
        }
    }

    private String indent(int length) {
        StringBuilder sb = new StringBuilder("  ");
        for (int i = 0; i < length; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    /**
     * 获取路由
     */
    public Route getRoute(String url) {
        Route route = exactRoutes.get(url);
        if (route != null) {
            return route;
        }
        //精准路由表没有查到，查找参数路由表
        Set<String> regexUrls = regexRoutes.keySet();
        for (String regexUrl : regexUrls) {
            if (Pattern.matches(regexUrl, url)) {
                return regexRoutes.get(regexUrl);
            }
        }
        return null;
    }

    /**
     * 判断URL中是否包含路径参数
     */
    private boolean hasPathParam(String url) {
        Pattern pattern = Pattern.compile("\\{\\w+}");
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    /**
     * <pre>
     * 传入/user/{userId} 输出/user/([^/]+)
     * URL中可以有特殊字符，具体有哪些可以参见 RFC1738，这里为了简单起见，只要不是 / 都可以
     * 这样会带来一些问题，比如
     * /user/{userId}-{blogId}
     * /user/{userId}
     * 这两个路由
     * /user/3-7 两个都匹配，当访问/user/3-7的时候，因为URL是依次匹配正则，实际匹配的是哪一个URL，则是看谁在前面了
     * </pre>
     */
    private static String getRegexUrl(String paramUrl) {
        return paramUrl.replaceAll("\\{\\w+}", "([^/]+)");
    }

}
