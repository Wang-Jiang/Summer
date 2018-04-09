package space.wangjiang.summer.controller;

import space.wangjiang.summer.route.NotRoute;

/**
 * Created by WangJiang on 2017/9/26.
 * 这是对Controller的扩展，让Summer支持RESTful的路由设计
 */
public abstract class RESTfulController extends Controller {

    public void index() {
        String method = getMethod().toLowerCase();
        switch (method) {
            case "get":
                get();
                break;
            case "post":
                post();
                break;
            case "put":
                put();
                break;
            case "delete":
                delete();
                break;
            default:
                //其他请求方式返回405
                render405();
                break;
        }
    }

    @NotRoute
    public abstract void get();

    @NotRoute
    public abstract void post();

    @NotRoute
    public abstract void put();

    @NotRoute
    public abstract void delete();

}
