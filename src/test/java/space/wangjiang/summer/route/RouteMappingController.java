package space.wangjiang.summer.route;

import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.lab.RouteMapping;

@SuppressWarnings("unused")
@RouteMapping(url = "/route")
public class RouteMappingController extends Controller {

    public void index() {
        renderText("RouteMappingController");
    }

    @UrlMapping(url = "/route/test/{id}")
    public void test0() {
        renderText(getPathPara("id"));
    }

    @UrlMapping(url = "/route/test/{userId}-{blogId}")
    public void test1() {
        renderText(getPathPara("userId") + "-" + getPathPara("blogId"));
    }

    /**
     * 如果url是/route/test/{blogId}.html会和下面的/{userId}/test/{blogId}.html冲突
     * 访问/route/test/36.html，两个实际上都是匹配的
     */
    @UrlMapping(url = "/route/test/{blogId}.htm")
    public void test2() {
        renderText(getPathPara("blogId"));
    }

    @UrlMapping(url = "/{userId}/test/{blogId}.html")
    public void test3() {
        renderText(getPathPara("userId") + "-" + getPathPara("blogId"));
    }

}
