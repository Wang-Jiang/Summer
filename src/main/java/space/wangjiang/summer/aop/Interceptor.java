package space.wangjiang.summer.aop;

/**
 * Created by WangJiang on 2017/9/9.
 * 拦截器
 */
public interface Interceptor {

    /**
     * 通过返回值来判断是否通过拦截器
     * 如果返回false，就不再执行后面的拦截器
     */
    boolean handle(Bundle bundle);

}
