package space.wangjiang.summer.aop;

import space.wangjiang.summer.controller.Controller;

/**
 * Created by WangJiang on 2017/9/9.
 * 用于拦截非GET请求
 */
public class GET implements Interceptor {

    @Override
    public boolean handle(Bundle bundle) {
        Controller controller = bundle.getController();
        if (controller.getMethod().toUpperCase().equals("GET")) {
            return true;
        }
        controller.render405();
        return false;
    }
}
