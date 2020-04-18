package space.wangjiang.summer.common;

import space.wangjiang.easylogger.json.JsonUtil;
import space.wangjiang.easylogger.json.handler.IJsonHandler;
import space.wangjiang.summer.model.Model;

public class ModelJsonHandler implements IJsonHandler<Model<?>> {

    @Override
    public String toJson(Model model) {
        return JsonUtil.map2Json(model.getAttrs());
    }

}
