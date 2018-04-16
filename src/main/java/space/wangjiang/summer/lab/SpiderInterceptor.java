package space.wangjiang.summer.lab;

import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.aop.Bundle;
import space.wangjiang.summer.aop.Interceptor;
import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.plugin.Plugin;
import space.wangjiang.summer.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by WangJiang on 2017/9/21.
 * 反爬虫拦截器，简单的IP过滤
 */
public class SpiderInterceptor implements Interceptor, Plugin {

    /**
     * 因为搜索引擎也会爬取页面，可以加入白名单中
     */
    private static final List<String> whiteList = new LinkedList<>();

    /**
     * 第一个是ip，第二个是加入黑名单的时间
     */
    private static final Map<String, Long> blackList = new HashMap<>();

    /**
     * 间隔多少秒，清空访问Ip列表
     */
    private static int CLEAN_VISIT_IP_SPAN_SECONDS = 60;

    /**
     * 黑名单IP的过期时间，需要注意这个只是一个大概的时间
     * 可以参见下面的CLEAN_BLACK_LIST_SPAN_SECONDS
     */
    private static int BLACK_LIST_IP_TIME_OUT_SECONDS = 1800;

    /**
     * 间隔多少秒，扫描黑名单，去除过期的ip
     * 在最差情况下，一个IP要经过BLACK_LIST_IP_TIME_OUT_SECONDS + CLEAN_BLACK_LIST_SPAN_SECONDS才会被从黑名单中移除
     * 因此扫描黑名单的间隔时间应当适当短一点
     */
    private static int SCAN_BLACK_LIST_SPAN_SECONDS = 600;

    /**
     * 每个时间间隔最大访问次数
     */
    private static int MAX_PER_SPAN = 20;

    /**
     * 清空访问IP的计时器
     */
    private static final Timer cleanVisitIpTimer = new Timer();

    /**
     * 清除黑名单的计时器
     */
    private static final Timer cleanBlackListTimer = new Timer();

    /**
     * IP和单位时间内的访问次数
     * 也就是说这个是单位时间会清空的
     * 然后每次请求的时候，判断有没有超过阈值的IP，加入黑名单
     * 每个时间间隔会清空一次
     */
    private static final Map<String, Integer> visitIp = new ConcurrentHashMap<>();

    /**
     * 显示的爬虫页面
     */
    private static String errorPage = null;

    @Override
    public boolean handle(Bundle bundle) {
        Controller controller = bundle.getController();
        String ip = getIp(controller.getRequest());
        if (blackList.containsKey(ip)) {
            if (errorPage == null) {
                controller.renderError(403);
            } else {
                controller.renderError(403, errorPage);
            }
            return false;
        }
        //不是黑名单的，将ip访问次数+1
        Integer count = visitIp.get(ip);
        if (count == null) {
            visitIp.put(ip, 1);
            return true;
        }
        count++;
        if (count > MAX_PER_SPAN) {
            //访问次数超过单位时间阈值，直接加入黑名单
            blackList.put(ip, System.currentTimeMillis());
            EasyLogger.warn("增加黑名单：" + ip);
            return false;
        }
        visitIp.put(ip, count);
        return true;
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtil.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtil.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    @Override
    public boolean start() {
        //从现在起过delay毫秒以后，每隔period毫秒执行一次。
        cleanVisitIpTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //清空
                EasyLogger.debug("清空所有访问IP列表");
                visitIp.clear();
            }
        }, 1000, CLEAN_VISIT_IP_SPAN_SECONDS * 1000);

        //扫描黑名单的Timer
        cleanBlackListTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //检查黑名单中有没有超时的IP，移除
                long currentTimeMillis = System.currentTimeMillis();
                long timeOutSpan = BLACK_LIST_IP_TIME_OUT_SECONDS * 1000;
                Iterator<Map.Entry<String, Long>> iterator = blackList.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Long> entry = iterator.next();
                    if ((currentTimeMillis - entry.getValue()) > timeOutSpan) {
                        EasyLogger.info("移除黑名单：" + entry.getKey());
                        iterator.remove(); //超时移除
                    }
                }
            }
        }, 1000, SCAN_BLACK_LIST_SPAN_SECONDS * 1000);

        EasyLogger.debug("爬虫拦截器启动");
        return true;
    }

    @Override
    public boolean stop() {
        EasyLogger.debug("爬虫拦截器停止");
        cleanVisitIpTimer.cancel();
        cleanBlackListTimer.cancel();
        return true;
    }

    public static void setCleanVisitIpSpanSeconds(int cleanVisitIpSpanSeconds) {
        CLEAN_VISIT_IP_SPAN_SECONDS = cleanVisitIpSpanSeconds;
    }

    public static void setMaxPerSpan(int maxPerSpan) {
        MAX_PER_SPAN = maxPerSpan;
    }

    public static void setErrorPage(String errorPage) {
        SpiderInterceptor.errorPage = errorPage;
    }

    public static void setBlackListIpTimeOutSeconds(int blackListIpTimeOutSeconds) {
        BLACK_LIST_IP_TIME_OUT_SECONDS = blackListIpTimeOutSeconds;
    }

    public static void setScanBlackListSpanSeconds(int scanBlackListSpanSeconds) {
        SCAN_BLACK_LIST_SPAN_SECONDS = scanBlackListSpanSeconds;
    }

}