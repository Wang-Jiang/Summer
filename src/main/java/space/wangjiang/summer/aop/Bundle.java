package space.wangjiang.summer.aop;

import space.wangjiang.summer.controller.Controller;

import java.lang.reflect.Method;

/**
 * Created by WangJiang on 2017/9/14.
 * 封装数据，便于拦截器内的代码调用
 */
public class Bundle {

    private Controller controller;
    private Method method;
    private String url;

    public Bundle(Controller controller, Method method, String url) {
        this.controller = controller;
        this.method = method;
        this.url = url;
    }

    @SuppressWarnings("unchecked")
    public <T extends Controller> T getController() {
        return (T) controller;
    }

    public Method getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

}
