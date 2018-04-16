package space.wangjiang.summer.plugin;

/**
 * Created by WangJiang on 2017/9/10.
 */
public class TestPlugin implements Plugin {

    @Override
    public boolean start() {
        System.out.println("自定义插件启动");
        return true;
    }

    @Override
    public boolean stop() {
        System.out.println("自定义插件销毁");
        return true;
    }
}
