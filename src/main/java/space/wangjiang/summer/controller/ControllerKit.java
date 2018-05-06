package space.wangjiang.summer.controller;

import space.wangjiang.summer.route.NotRoute;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangJiang on 2017/9/10.
 * Controller的一些辅助类
 */
public class ControllerKit {

    /**
     * 获取Controller中的所有路由方法
     * 路由方法就是返回值为void，方法参数为空的public方法
     * 并且没有标记NotRoute的注解
     */
    public static List<Method> getRouteMethod(Class<? extends Controller> clazz) {
        List<Method> list = new ArrayList<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            //通过反射，获取类定义的公有的无参数方法，不是静态的，并且没有标记NotRoute的注解
            int modifiers = method.getModifiers();
            if (method.getParameterCount() == 0
                    && Modifier.isPublic(modifiers)
                    && method.getReturnType() == Void.TYPE
                    && !Modifier.isStatic(modifiers)
                    && method.getAnnotation(NotRoute.class) == null) {
                list.add(method);
            }
        }
        return list;
    }

}
