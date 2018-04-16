package space.wangjiang.summer.lab;

import space.wangjiang.summer.config.SummerConfig;
import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.plugin.Plugin;
import space.wangjiang.summer.scanner.AbsClassScanner;
import space.wangjiang.summer.util.StringUtil;

import java.util.List;

/**
 * 用于配置标记了@RouteMapping的Controller，这样就不需要在SummerConfig中配置了
 */
@SuppressWarnings("unchecked")
public class RouteMappingPlugin implements Plugin {

    private String basePackage;

    public RouteMappingPlugin(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public boolean start() {
        //扫描所有Controller
        AbsClassScanner scanner = new AbsClassScanner(basePackage) {
            @Override
            public boolean accept(Class<?> clazz) {
                //标记RouteMapping的控制器
                return clazz.isAnnotationPresent(RouteMapping.class)
                        && Controller.class.isAssignableFrom(clazz)
                        && !Controller.class.equals(clazz);
            }
        };
        List<Class<?>> list = scanner.getClassList();
        for (Class clazz : list) {
            RouteMapping mapping = (RouteMapping) clazz.getAnnotation(RouteMapping.class);
            addRoute(mapping.url(), clazz, mapping.viewPath());
        }
        return true;
    }

    private void addRoute(String url, Class<? extends Controller> clazz, String viewPath) {
        if (StringUtil.isEmpty(viewPath)) {
            SummerConfig.config.getRouteConfig().addRoute(url, clazz, viewPath);
        } else {
            SummerConfig.config.getRouteConfig().addRoute(url, clazz);
        }
    }

    @Override
    public boolean stop() {
        return true;
    }
}
