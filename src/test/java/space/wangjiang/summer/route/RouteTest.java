package space.wangjiang.summer.route;

import org.junit.Assert;
import org.junit.Test;
import space.wangjiang.summer.test.BaseTest;
import space.wangjiang.summer.util.StringUtil;

public class RouteTest extends BaseTest {

    private final String BASE_URL = "http://127.0.0.1:8080/";

    @Test
    public void test0() {
        for (int i = 0; i < 10; i++) {
            String id = StringUtil.getRandomNumber(4);
            Assert.assertEquals(id, get(BASE_URL + "route/test/" + id, null));
        }
    }

    @Test
    public void test1() {
        for (int i = 0; i < 10; i++) {
            String userId = StringUtil.getRandomNumber(4);
            String blogId = StringUtil.getRandomNumber(4);
            String url = String.format(BASE_URL + "route/test/%s-%s", userId, blogId);
            Assert.assertEquals(userId + "-" + blogId, get(url, null));
        }
    }

    @Test
    public void test2() {
        for (int i = 0; i < 10; i++) {
            String id = StringUtil.getRandomNumber(4);
            Assert.assertEquals(id, get(BASE_URL + "route/test/" + id + ".htm", null));
        }
    }

    @Test
    public void test3() {
        for (int i = 0; i < 10; i++) {
            String userId = StringUtil.getRandomNumber(4);
            String blogId = StringUtil.getRandomNumber(4);
            String url = String.format(BASE_URL + "%s/test/%s.html", userId, blogId);
            Assert.assertEquals(userId + "-" + blogId, get(url, null));
        }
    }

}
