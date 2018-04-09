package space.wangjiang.summer.config;

import space.wangjiang.summer.constant.ConstantConfig;
import space.wangjiang.summer.lab.RouteMappingPlugin;
import space.wangjiang.summer.model.ModelConfig;
import space.wangjiang.summer.plugin.PluginConfig;
import space.wangjiang.summer.route.RouteConfig;
import space.wangjiang.summer.form.FormController;
import space.wangjiang.summer.controller.IndexController;
import space.wangjiang.summer.controller.RESTfulApiController;
import space.wangjiang.summer.model.MappingKit;
import space.wangjiang.summer.model.ModelTestUtil;
import space.wangjiang.summer.plugin.TestPlugin;

public class TestConfig extends SummerConfig {

    @Override
    public void initConstant(ConstantConfig config) {
        config.setDevMode(true);
    }

    @Override
    public void initRoute(RouteConfig config) {
        config.addRoute("/", IndexController.class);
        config.addRoute("/api", RESTfulApiController.class);
        config.addRoute("/form", FormController.class);
    }

    @Override
    public void initModel(ModelConfig config) {
        ModelTestUtil.configMySql(config);
        MappingKit.mapping(config);
    }

    @Override
    public void initPlugin(PluginConfig config) {
        config.addPlugin(new TestPlugin());
        config.addPlugin(new RouteMappingPlugin("space.wangjiang.summer"));
    }
}
