package space.wangjiang.summer.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WangJiang on 2017/9/9.
 * Summer的基础配置
 */
public class SummerCommon {

    //START_LOGO由该网站生成  http://patorjk.com/software/taag/#p=display&f=Graffiti&t=Type%20Something%20
    public static final String START_LOGO = new ResourceReader().read("StartLogo.txt");
    private static String BASE_HTML = new ResourceReader().read("ErrorPage.html");
    public static final String NAME = "Summer";
    public static final String VERSION = "0.1.1";
    private static Map<Integer, String> errorHtmlMap = new HashMap<>();

    static {
        addBaseErrorHtml(400, "Bad Request 服务器不理解请求的语法");
        addBaseErrorHtml(401, "Unauthorized 请求要求身份验证");
        addBaseErrorHtml(403, "Forbidden 服务器拒绝请求");
        addBaseErrorHtml(404, "Not Found 页面没有找到");
        addBaseErrorHtml(405, "Method Not Allowed 请求方式错误");
        addBaseErrorHtml(500, "Internal Server Error 服务器出错");
        addBaseErrorHtml(501, "Not Implemented 服务器不具备完成请求的功能");
        addBaseErrorHtml(502, "Bad Gateway 错误网关");
        addBaseErrorHtml(503, "Service Unavailable 服务不可用");
    }

    private static void addBaseErrorHtml(int code, String msg) {
        errorHtmlMap.put(code, renderBaseErrorHtml(code, msg));
    }

    /**
     * 渲染基本的错误页面
     */
    private static String renderBaseErrorHtml(int code, String msg) {
        return String.format(BASE_HTML, code, code + "，" + msg, NAME + " " + VERSION);
    }

    /**
     * 获取错误页面的html
     */
    public static String getBaseErrorHtml(Integer code) {
        return errorHtmlMap.get(code);
    }

}
