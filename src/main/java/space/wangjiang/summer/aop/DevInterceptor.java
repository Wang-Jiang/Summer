package space.wangjiang.summer.aop;

import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.controller.Controller;

/**
 * 开发模式用的拦截器，用于输出请求的所有参数和总共执行时间
 * 请求执行前记录当前时间，方法和所有拦截器执行完成之后，输出总共耗时
 * Dev模式时，Summer会将DevInterceptor放到beforeInterceptors的第一个，和afterInterceptors的最后一个
 */
public class DevInterceptor implements Interceptor {

    private static final String START_TIME = "start_time_is_set_by_dev_interceptor";

    @Override
    public boolean handle(Bundle bundle) {
        Controller controller = bundle.getController();
        Long startTime = controller.getAttr(START_TIME);
        if (startTime == null) {
            //before
            controller.setAttr(START_TIME, System.currentTimeMillis());
        } else {
            //after
            //输出所有的参数和执行时间
            Logger.printRequestInfo(controller.getRequest(), controller.getClass(), bundle.getMethod(), startTime);
        }
        return true;
    }

}
