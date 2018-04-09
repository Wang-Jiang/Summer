package space.wangjiang.summer.aop;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WangJiang on 2017/9/28.
 * 管理Summer的所有拦截器实例
 */
public class InterceptorManager {

    /**
     * 所有拦截器实例
     */
    private static Map<Class<? extends Interceptor>, Interceptor> interceptorPool = new HashMap<>();

    /**
     * 获取拦截器实例，如果没有，生成实例，并放到池中
     */
    public static Interceptor getInstance(Class<? extends Interceptor> clazz) {
        Interceptor interceptor = interceptorPool.get(clazz);
        if (interceptor == null) {
            try {
                //不存在，实例化一个，放到池中
                interceptor = clazz.newInstance();
                interceptorPool.put(clazz, interceptor);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("拦截器生成实例失败:" + clazz.getName(), e);
            }
        }
        return interceptor;
    }

}
