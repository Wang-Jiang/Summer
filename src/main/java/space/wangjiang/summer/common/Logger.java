package space.wangjiang.summer.common;

import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.config.SummerConfig;
import space.wangjiang.summer.route.Route;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;

/**
 * Created by WangJiang on 2017/9/8.
 * 简单的Log工具
 */
public class Logger {

    public static void debug(String msg) {
        if (SummerConfig.config == null || SummerConfig.config.getConstantConfig().isDevMode()) {
            System.out.println("DEBUG : " + msg);
        }
    }

    public static void debug(String tag, String msg) {
        if (SummerConfig.config == null || SummerConfig.config.getConstantConfig().isDevMode()) {
            System.out.println("DEBUG/" + tag + " : " + msg);
        }
    }

    public static void complete() {
        if (SummerConfig.config == null || SummerConfig.config.getConstantConfig().isDevMode()) {
            System.out.println("COMPLETE Y(^_^)Y");
        }
    }

    public static void printRequestInfo(HttpServletRequest request, Route route, long startTime) {
        printRequestInfo(request, route.getControllerClass(), route.getMethod(), startTime);
    }

    /**
     * 打印请求信息
     */
    public static void printRequestInfo(HttpServletRequest request, Class controllerClass, Method method, long startTime) {
        Enumeration<String> names = request.getParameterNames();
        StringBuilder msg = new StringBuilder();
        msg.append("Request : ").append(request.getRequestURI()).append("\n");
        msg.append("Method : ").append(controllerClass.getName())
                .append(".").append(method.getName())
                .append("(").append(controllerClass.getSimpleName()).append(".java:1)\n");
        msg.append("┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈\n");
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            msg.append(name).append(" : ").append(request.getParameter(name)).append("\n");
        }
        msg.append("┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈\n");
        msg.append("Time : ").append(System.currentTimeMillis() - startTime);
        EasyLogger.debug(msg);
    }

}
