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
        renderText("match /route/test/{id} id:" + getPathPara("id"));
    }

    @UrlMapping(url = "/route/test/{userId}-{blogId}")
    public void test1() {
        renderText("match /route/test/{userId}-{blogId} " +
                "userId:" + getPathPara("userId") +
                " blogId:" + getPathPara("blogId"));
    }

    @UrlMapping(url = "/route/test/{userId}-{blogId}-{commentId}")
    public void test2() {
        renderText("match /route/test/{userId}-{blogId}-{commentId} " +
                "userId:" + getPathPara("userId") +
                " blogId:" + getPathPara("blogId") +
                " commentId:" + getPathPara("commentId"));
    }

    @UrlMapping(url = "/route/test/{userId}.html")
    public void test3() {
        renderText("match /route/test/{userId}.html " +
                "userId:" + getPathPara("userId"));
    }

    /**
     * 如果url是/route/test/{blogId}.html会和下面的/{userId}/test/{blogId}.html冲突
     * 访问/route/test/36.html，两个实际上都是匹配的
     */
    @UrlMapping(url = "/route/test/{blogId}.htm")
    public void test4() {
        renderText(getPathPara("blogId"));
    }

    @UrlMapping(url = "/{userId}/test/{blogId}.html")
    public void test5() {
        renderText(getPathPara("userId") + "-" + getPathPara("blogId"));
    }

}
