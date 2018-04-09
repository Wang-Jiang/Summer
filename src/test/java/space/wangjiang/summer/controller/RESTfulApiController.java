package space.wangjiang.summer.controller;

/**
 * Created by WangJiang on 2017/9/26.
 * RESTfulController的测试
 */
public class RESTfulApiController extends RESTfulController {

    @Override
    public void get() {
        renderText("GET");
    }

    @Override
    public void post() {
        renderText("POST");
    }

    @Override
    public void put() {
        renderText("PUT");
    }

    @Override
    public void delete() {
        renderText("DELETE");
    }

}
