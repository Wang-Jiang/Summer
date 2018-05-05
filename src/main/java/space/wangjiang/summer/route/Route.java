package space.wangjiang.summer.route;

import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.aop.*;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.config.SummerConfig;
import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.form.CheckForm;
import space.wangjiang.summer.form.Form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WangJiang on 2017/9/10.
 * 路由:URL-->METHOD
 */
public class Route {

    private String url;
    private String regexUrl;  //TODO 这个没有考虑到传入是url中包含有正则表达式的元字符,${}^等
    private String baseViewPath; //renderJsp的基础路径
    private Class<? extends Controller> controllerClass;
    private Method method;
    private boolean isParamUrl;  //是否是参数Url
    private List<String> params; //参数的列表 userId blogId等等

    private List<Interceptor> beforeInterceptors = new ArrayList<>();  //所有的Before拦截器
    private List<Interceptor> afterInterceptors = new ArrayList<>();   //所有的After拦截器

    /**
     * 精准路由，精准路由的正则化就是本身
     */
    public Route(String url, Class<? extends Controller> controllerClass, Method method, String baseViewPath) {
        this(url, url, controllerClass, method, false, baseViewPath);
    }

    /**
     * 正则路由
     */
    public Route(String url, String regexUrl, Class<? extends Controller> controllerClass, Method method, String baseViewPath) {
        this(url, regexUrl, controllerClass, method, true, baseViewPath);
    }

    /**
     * @param url             原始的URL，例如带参数的url，/{blogId}.html
     * @param regexUrl        正则化的Url，精准路由的正则化就是本身
     * @param controllerClass Controller类
     * @param method          方法
     * @param isParamUrl      是否是参数路由
     * @param baseViewPath    基础的ViewPath
     */
    private Route(String url, String regexUrl, Class<? extends Controller> controllerClass, Method method, boolean isParamUrl, String baseViewPath) {
        this.url = url;
        this.regexUrl = regexUrl;
        this.controllerClass = controllerClass;
        this.method = method;
        this.isParamUrl = isParamUrl;

        //baseViewPath应该以/开头，以/结尾
        if (baseViewPath != null) {
            if (!baseViewPath.startsWith("/")) {
                baseViewPath = "/" + baseViewPath;
            }
            if (!baseViewPath.endsWith("/")) {
                baseViewPath = baseViewPath + "/";
            }
        }
        this.baseViewPath = baseViewPath;

        if (isParamUrl) {
            //找出所有的url参数
            params = new ArrayList<>();
            Pattern pattern = Pattern.compile("\\{\\w+}");
            Matcher matcher = pattern.matcher(url);
            while (matcher.find()) {
                String param = matcher.group()
                        .replace("{", "")
                        .replace("}", "");
                params.add(param);
            }
        }

        //处理AOP注解，需要注意的是，Remove和Before的顺序很关键，比如
        //@Remove()
        //@Before(GET.class)
        //表示的就是先移除所有的拦截器，再加上GET拦截器

        //处理类上的AOP注解
        processAopAnnotations(controllerClass.getAnnotations());
        //处理方法上的AOP注解，
        processAopAnnotations(method.getAnnotations());

        //Dev模式时，添加Dev拦截器，输出请求的参数和方法执行时间
        if (SummerConfig.config.getConstantConfig().isDevMode()) {
            beforeInterceptors.add(0, InterceptorManager.getInstance(DevInterceptor.class));
            afterInterceptors.add(InterceptorManager.getInstance(DevInterceptor.class));
        }
    }

    /**
     * AOP注解可以用于类上和方法上面
     */
    private void processAopAnnotations(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Remove) {
                Class<? extends Interceptor>[] interceptorClasses = ((Remove) annotation).value();
                if (interceptorClasses.length == 0) {
                    //默认清除所有拦截器
                    beforeInterceptors.clear();
                }
                for (Class<? extends Interceptor> clazz : interceptorClasses) {
                    beforeInterceptors.remove(InterceptorManager.getInstance(clazz));
                }
            }
            //添加Before拦截器
            if (annotation instanceof Before) {
                addInterceptors(beforeInterceptors, ((Before) annotation).value());
            }
            //添加After拦截器
            if (annotation instanceof After) {
                addInterceptors(afterInterceptors, ((After) annotation).value());
            }
        }
    }

    /**
     * 获取classes的拦截器实例，放到拦截器列表
     *
     * @param interceptors 拦截器列表
     * @param classes      拦截器的类
     */
    private void addInterceptors(List<Interceptor> interceptors, Class<? extends Interceptor>[] classes) {
        for (Class<? extends Interceptor> clazz : classes) {
            //Interceptor是单例的
            Interceptor interceptor = InterceptorManager.getInstance(clazz);
            if (!interceptors.contains(interceptor)) {
                //防止重复增加
                interceptors.add(interceptor);
            }
        }
    }

    /**
     * 调用控制器的方法
     */
    public void invoke(HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        //实例化controller
        Controller controller = getControllerClass().newInstance();
        controller.init(request, response, baseViewPath);

        //最先处理参数URL，这样拦截器中也可以正常获取参数
        if (isParamUrl) {
            Matcher matcher = Pattern.compile(regexUrl).matcher(request.getRequestURI());
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    //按顺序设置所有的参数，放到Attribute中
                    request.setAttribute(params.get(i - 1), matcher.group(i));
                }
            }
        }

        Bundle bundle = new Bundle(controller, method, url);
        //处理Before拦截器
        if (!handleInterceptors(bundle, beforeInterceptors)) {
            return;
        }

        //处理@CheckForm注解，如果通过，将表单放到Attribute中
        CheckForm checkForm = method.getAnnotation(CheckForm.class);
        if (checkForm != null) {
            Logger.debug("Checking form");
            Class<? extends Form> formClass = checkForm.value();
            Form form = Form.getNewForm(controller, formClass);
            if (form.isNotValid()) {
                form.renderError(controller, form.getErrorMsg());
                return;
            }
            request.setAttribute(Form.ATTRIBUTE_FORM_NAME, form);
        }

        //最核心的调用
        method.invoke(controller);

        //处理After拦截器
        if (!handleInterceptors(bundle, afterInterceptors)) {
            return;
        }
    }

    /**
     * 如果所有拦截器都处理完成，返回true
     */
    private boolean handleInterceptors(Bundle bundle, List<Interceptor> interceptors) {
        for (Interceptor interceptor : interceptors) {
            if (!interceptor.handle(bundle)) {
                //有拦截器失败了，直接返回
                EasyLogger.error(String.format("拦截器%s返回False", interceptor.getClass().getSimpleName()));
                return false;
            }
        }
        return true;
    }

    //getter方法

    public String getUrl() {
        return url;
    }

    public Class<? extends Controller> getControllerClass() {
        return controllerClass;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isParamUrl() {
        return isParamUrl;
    }

    public String getBaseViewPath() {
        return baseViewPath;
    }
}
