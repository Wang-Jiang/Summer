package space.wangjiang.summer.plugin;

import space.wangjiang.easylogger.EasyLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangJiang on 2017/9/10.
 * 插件配置
 */
public class PluginConfig {

    private List<Plugin> plugins = new ArrayList<>();

    public void addPlugin(Plugin plugin) {
        plugins.add(plugin);
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void startPlugins() {
        for (Plugin plugin : plugins) {
            boolean success = plugin.start();
            if (!success) {
                //插件启动失败
                throw new RuntimeException("插件启动失败:" + plugin.getClass().getName());
            }
        }
    }

    public void stopPlugins() {
        for (Plugin plugin : plugins) {
            boolean success = plugin.stop();
            if (!success) {
                //插件停止失败
                EasyLogger.error("插件停止失败:" + plugin.getClass().getName());
            }
        }
    }

}
