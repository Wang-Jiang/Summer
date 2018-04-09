package space.wangjiang.summer.model;

import space.wangjiang.easylogger.EasyLogger;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by WangJiang on 2017/10/3.
 * 非Web环境下Model使用测试
 */
public class NonWebModelTest {

    @Before
    public void init() {
        ModelConfig config = ModelTestUtil.getModelConfig();
        config.addMapping("blog", "id", Blog.class);
        config.addMapping("user", "id", User.class);
        config.addMapping("like", "blog_id,user_id", Like.class);

        EasyLogger.showCallMethodAndLine(false);
    }

    @Test
    public void test() {
        List<User> list = User.DAO.find("select * from user");
        EasyLogger.json(list);
    }

}
