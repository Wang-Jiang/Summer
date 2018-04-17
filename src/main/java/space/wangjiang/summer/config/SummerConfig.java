package space.wangjiang.summer.config;

import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.constant.ConstantConfig;
import space.wangjiang.summer.controller.ErrorController;
import space.wangjiang.summer.model.ModelConfig;
import space.wangjiang.summer.plugin.PluginConfig;
import space.wangjiang.summer.route.Route;
import space.wangjiang.summer.route.RouteConfig;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by WangJiang on 2017/9/8.
 * 配置
 */
public abstract class SummerConfig implements Filter {

    //维持了一个配置，方便其他类查询配置
    public static SummerConfig config = null;
    //常量配置
    private ConstantConfig constantConfig = new ConstantConfig();
    //路由表
    private RouteConfig routeConfig = new RouteConfig();
    //初始化空白的数据库配置
    private ModelConfig modelConfig = new ModelConfig();
    //插件配置
    private PluginConfig pluginConfig = new PluginConfig();

    //Prop配置文件
    private Prop prop = null;

    private ServletContext servletContext;

    /**
     * 加载Properties文件
     */
    public void loadPropFile(String fileName) {
        prop = new Prop(fileName);
    }

    /**
     * 初始化常量配置
     * <p>
     * 这个需要最先调用，因为loadPropFile方法一般在这下面调用
     */
    public abstract void initConstant(ConstantConfig config);

    /**
     * 初始化配置路由
     */
    public abstract void initRoute(RouteConfig config);

    /**
     * 初始化配置Model
     */
    public abstract void initModel(ModelConfig config);

    /**
     * 初始化插件
     */
    public abstract void initPlugin(PluginConfig config);

    /**
     * 初始化ConstantConfig，设置默认上传参数
     */
    private void initConstant() {
        constantConfig.setBaseUploadPath("upload");
        initConstant(constantConfig);
    }

    /**
     * 初始化插件，并启动插件列表
     */
    private void initPlugin() {
        initPlugin(pluginConfig);
        pluginConfig.startPlugins();
    }

    private void initEasyLogger() {
        EasyLogger.showCallMethodAndLine(false);
        EasyLogger.showTime(true);
    }

    /**
     * 过滤器只会初始化一次
     */
    @Override
    public void init(FilterConfig filterConfig) {
        //SummerConfig初始化
        config = this;
        servletContext = filterConfig.getServletContext();

        initEasyLogger();

        initConstant();
        initRoute(routeConfig);
        initModel(modelConfig);
        initPlugin();

        Logger.debug("Summer initialization completed");
        System.out.println(constantConfig.getStartLogo());
    }

    /**
     * <pre>
     * 如果web.xml配置的是/*，所有的请求都会通过这个地方，包括 各种图片、css请求
     * 这些请求会先查询精准路由表，然后查询参数路由表，最后交给Tomcat等服务器处理
     * 不过实际测试发现对页面相应时间几乎没有影响
     * 而且一般都是Nginx或者apache去处理静态文件，到Tomcat的都是action请求
     * 再去判断反倒是没有必要
     * </pre>
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        request.setCharacterEncoding(constantConfig.getEncoding()); //设置编码
        response.setCharacterEncoding(constantConfig.getEncoding());

        String url = request.getRequestURI();
        Route route = routeConfig.getRoute(url); //查路由表
        if (route == null) {
            //没有查到路由，也有可能是访问静态文件
            if (url.lastIndexOf('.') > url.lastIndexOf('/')) {
                //最后一个URL带有 . ，当做静态类文件，交给Tomcat处理
                filterChain.doFilter(request, response);
                return;
            }
            //没有找到路由，并且不是访问文件，404啦
            ErrorController errorController = new ErrorController();
            errorController.init(request, response, null);
            errorController.render404();
            return;
        }
        try {
            //调用URL
            route.invoke(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorController errorController = new ErrorController();
            errorController.init(request, response, null);
            errorController.render500();
        }
        if (constantConfig.isDevMode()) {
            Logger.printRequestInfo(request, route, startTime);
        }
    }

    //getter方法

    public ModelConfig getModelConfig() {
        return modelConfig;
    }

    /**
     * 这个是为了解决在非Web环境下使用Model功能
     * 这样就不需要配置SummerConfig，只需要配置ModelConfig就可以正常使用Model
     *
     * @see ModelConfig#config
     */
    public static ModelConfig getModelConfigStatically() {
        if (config != null) {
            return config.getModelConfig();
        }
        return ModelConfig.config;
    }

    public ConstantConfig getConstantConfig() {
        return constantConfig;
    }

    public RouteConfig getRouteConfig() {
        return routeConfig;
    }

    public Prop getProp() {
        return prop;
    }

    public String getProperty(String key) {
        if (prop == null) return null;
        return prop.getStr(key);
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * 当tomcat关闭之后会调用
     */
    @Override
    public void destroy() {
        //停止所有的插件
        pluginConfig.stopPlugins();
        modelConfig.destroy();
        Logger.debug("Summer has been destroyed");
    }

}
