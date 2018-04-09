package space.wangjiang.summer.plugin;

/**
 * Created by WangJiang on 2017/9/10.
 * 插件接口
 */
public interface IPlugin {

    /**
     * 当插件返回false的时候，Summer将会停止启动，直接抛出异常
     */
    boolean start();

    boolean stop();

}
