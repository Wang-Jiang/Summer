package space.wangjiang.summer.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.model.provider.DefaultConnectionProvider;
import space.wangjiang.summer.model.provider.DruidConnectionProvider;
import space.wangjiang.summer.util.StringUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

/**
 * Created by WangJiang on 2017/9/30.
 * Model类的单元测试，Model是相当重要的功能，需要做完整的测试
 */
public class ModelTest {

    @Before
    public void init() {
        ModelConfig config = ModelTestUtil.getModelConfig();

        config.addMapping("blog", "XXX", Blog.class);
//        config.addMapping("user", "id  ", User.class);
//        config.addMapping("like", "blog_id  ,   user_id", Like.class);
        MappingKit.mapping(config);
        config.addMapping("like", "blog_id, user_id", Like.class);

        EasyLogger.showCallMethodAndLine(false);
        EasyLogger.setTag("ModelTest");
    }

    @After
    public void stop() {
        ModelConfig.config.destroy();
    }

    private void log(Object object) {
        if (object instanceof Model) {
            EasyLogger.json(((Model) object).toJson());
            return;
        }
        EasyLogger.json(object);
    }

    @Test
    public void findTest() {
        int userId = getRandomUserId();
        List<User> list = User.DAO.find("select * from user where id>=?", userId);
        for (User user : list) {
            assert user.getId() >= userId;
        }

        User user = User.DAO.findFirst("select * from user where id=?", userId);
        assert user.getId().equals(userId);

        user = User.DAO.findFirst("select * from user where username is not null and password is not null and age is not null and email is not null");
        log(user);
        String name = user.getUsername();
        String email = user.getEmail();
        String password = user.getPassword();
        int age = user.getAge();
        user = User.DAO.findFirst("select * from user where username=? and password=? and age=? and email=?", name, password, age, email);
        assert user.getAge() == age;
        assert user.getUsername().equals(name);

        EasyLogger.info("findTest pass");
    }

    @Test
    public void findByIdTest() {
        int userId = getRandomUserId();
        User user = User.DAO.findById(userId);
        assert user.getId().equals(userId);

        user = User.DAO.findById(String.valueOf(userId));
        assert user.getId().equals(userId);

        EasyLogger.info("findByIdTest pass");
    }

    @Test
    public void saveTest() {
        String name = getRandomString();
        String email = name + "@example.com";
        String password = getRandomString();
        int age = getRandomAge();
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setAge(age);
        user.setPassword(password);
        assert user.save();
//        assert !user.save(); //id已存在，报错，返回false

        int userId = user.getId();
        user = User.DAO.findById(userId);
        assert user.getAge().equals(age);
        assert user.getUsername().equals(name);
        assert user.getEmail().equals(email);
        assert user.getPassword().equals(password);

        //NULL测试
        user = new User();
        user.setAge(null);
        user.setUsername(null);
        user.setEmail(null);
        assert user.save();

        userId = user.getId();
        user = User.DAO.findById(userId);
        assert user.getPassword() == null;
        assert user.getAge() == null;
        assert user.getEmail() == null;
        assert user.getUsername() == null;

        String title = StringUtil.getRandomString(10);
        String content = StringUtil.getRandomString(50);
        Blog blog = new Blog();
        blog.setUserId(userId);
        blog.setTitle(title);
        blog.setContent(content);
        assert blog.save();

        int blogId = blog.getId();
        blog = Blog.DAO.findById(blogId);
        assert blog.getTitle().equals(title);
        assert blog.getContent().equals(content);
        assert blog.getUserId().equals(userId);

        EasyLogger.info("saveTest pass");
    }

    @Test
    public void deleteTest() {
        int userId = getRandomUserId();
        User user = User.DAO.findFirst("select * from user where id=?", userId);
        user.delete();

        user = User.DAO.findById(userId);
        assert user == null;

        EasyLogger.info("deleteTest pass");
    }

    @Test
    public void deleteByIdTest() {
        Integer randomId = getRandomUserId();
        User.DAO.deleteById(randomId);

        User user = User.DAO.findById(randomId);
        assert user == null;

        EasyLogger.info("deleteByIdTest pass");
    }

    @Test
    public void updateTest() {
        Integer userId = getRandomUserId();
        //各种类型的值的更新
        User user = User.DAO.findById(userId);
        int age = getRandomAge();
        String name = getRandomString();
        String password = getRandomString();
        user.setAge(age);
        user.setUsername(name);
        user.setPassword(password);
        user.update();

        user = User.DAO.findById(userId);
        assert user.getAge() == age;
        assert user.getUsername().equals(name);
        assert user.getPassword().equals(password);

        //当字段是null的时候的更新
        userId = getRandomUserId();
        user = User.DAO.findById(userId);
        user.setAge(null);
        user.setEmail(null);
        user.setUsername(null);
        user.update();

        user = User.DAO.findById(userId);
        assert user.getAge() == null;
        assert user.getEmail() == null;
        assert user.getUsername() == null;

        EasyLogger.info("updateTest pass");
    }

    @Test
    public void executeUpdateTest() {
        int userId = getRandomUserId();
        int age = getRandomAge();
        //执行更新
        User.DAO.executeUpdate("update user set age=? where id=?", age, userId);
        User user = User.DAO.findById(userId);
        assert user.getAge().equals(age);

        //执行删除
        userId = getRandomUserId();
        User.DAO.executeUpdate("delete from user where id=?", userId);
        user = User.DAO.findById(userId);
        assert user == null;

        //参数是null的测试
        String content = getRandomString();
        Blog blog = new Blog();
        blog.setTitle(null);
        blog.setContent(content);
        blog.setUserId(null);
        blog.save();
        Blog.DAO.executeUpdate("delete from blog where title is ?", (Object) null);
        assert Blog.DAO.find("select * from blog where title is null").size() == 0;

        blog.save();
        Blog.DAO.executeUpdate("delete from blog where title is ? and user_id is ?", null, null);
        assert Blog.DAO.find("select * from blog where title is null and user_id is null").size() == 0;
    }

    @Test
    public void pageTest() {
//        List<User> list = User.DAO.find("select * from user");
//        log(list);

        Page<User> page0 = User.DAO.page(5, 1, "select *",
                "from user order by id desc");
        log(page0);

        Page<User> page1 = User.DAO.page(5, 1, "select *",
                "from user where id>=? and id<?", 10, 20);
        log(page1);
    }

    /**
     * 各种get测试
     */
    @Test
    public void getTest() {
        User shortTest = User.DAO.findFirst("select * from user");
        assert shortTest.getShort("id").shortValue() == shortTest.getId();
        assert shortTest.getShort("not_exist") == null;

        User intTest = User.DAO.findFirst("select count(*) from user");
        assert intTest.getInt("count(*)").intValue() == intTest.getLong("count(*)");
        assert intTest.getInt("not_exist") == null;

        User longTest = User.DAO.findById(getRandomUserId());
        assert longTest.getLong("id").longValue() == longTest.getId();
        assert longTest.getLong("not_exist") == null;

        User floatTest = User.DAO.findById(getRandomUserId());
        assert floatTest.getFloat("id").floatValue() == new Float(floatTest.getId());
        assert floatTest.getFloat("not_exist") == null;

        User doubleTest = User.DAO.findFirst("select *,sum(age) from user");
        assert doubleTest.getInt("sum(age)").intValue() == doubleTest.getLong("sum(age)");
        assert doubleTest.getDouble("sum(age)").doubleValue() == new Double(doubleTest.getLong("sum(age)"));
        assert doubleTest.getDouble("not_exist") == null;

        EasyLogger.info("getTest pass");
    }

    /**
     * 联合主键测试
     */
    @Test
    public void compositePrimaryKeyTest() {
        int userId = getRandomUserId();
        int blogId = getRandomBlogId();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - 1000000000L);

        Like like = new Like();
        like.setBlogId(blogId);
        like.setUserId(userId);
        like.setCreateTime(timestamp);
        like.save();

        like = Like.DAO.findById(blogId, userId);

        assert like.getUserId().equals(userId);
        assert like.getBlogId().equals(blogId);
//        assert like.getCreateTime().equals(timestamp);//时间戳插入有时候会多一秒钟

        log(like.getCreateTime());
        log(timestamp);

        EasyLogger.info("compositePrimaryKeyFindTest");
    }

    @Test
    public void toStringTest() {
        User user = new User();
        user.setAge(10);
        user.setEmail("test@example.com");
        user.setPassword("123456");
        EasyLogger.debug(user.toString());
        assert user.toString().equals("{password:123456,age:10,email:test@example.com}");

        Blog blog = new Blog();
        EasyLogger.debug(blog.toString());
        assert blog.toString().equals("{}");

        EasyLogger.info("toStringTest pass");
    }

    @Test
    public void sqlInjectionTest() {
        Blog blog = new Blog();
        blog.setTitle("aa','1'='1");
        blog.setContent("");
        blog.save();
    }

    @Test
    public void keepTest() {
        User user = User.DAO.findById(getRandomUserId());
        EasyLogger.json(user);
        user.keep("id");
        EasyLogger.json(user);

        user = User.DAO.findById(getRandomUserId());
        user.keep("id", "email");
        EasyLogger.json(user);

        user.clear();
        EasyLogger.json(user);
    }

    /**
     * 字段别名
     */
    @Test
    public void asTest() {
        User user = User.DAO.findFirst("SELECT id,username AS name FROM user WHERE id=? LIMIT 1", getRandomUserId());
        EasyLogger.debug(user);
    }

    /**
     * 测试DefaultConnectionProvider和DruidConnectionProvider
     */
    @Test
    public void defaultConnectionProviderTest() {
        ModelConfig.config.setConnectionProvider(new DefaultConnectionProvider());
        task();
    }

    @Test
    public void druidConnectionProviderTest() {
        ModelConfig.config.setConnectionProvider(new DruidConnectionProvider());
        task();
    }

    private void task() {
        long start = System.currentTimeMillis();
        int userId = getRandomUserId();
        for (int i = 0; i < 200; i++) {
            User.DAO.findFirst("SELECT * FROM user WHERE id=" + userId);
        }
        EasyLogger.debug("耗时：" + (System.currentTimeMillis() - start));
    }

    /**
     * 获取一个随机的UserId
     */
    private Integer getRandomUserId() {
        Logger.debug("getRandomUserId====>start");
        List<User> list = User.DAO.find("select id from user");
        Logger.debug("getRandomUserId====>end");
        int randomIndex = new Random().nextInt(list.size());
        return list.get(randomIndex).getId();
    }

    private Integer getRandomBlogId() {
        Logger.debug("getRandomBlogId====>start");
        List<Blog> list = Blog.DAO.find("select id from blog");
        Logger.debug("getRandomBlogId====>end");
        int randomIndex = new Random().nextInt(list.size());
        return list.get(randomIndex).getId();
    }

    @SuppressWarnings("unused")
    private Integer getMaxId() {
        return User.DAO.findFirst("select max(id) from user").getInt("max(id)");
    }

    private String getRandomString() {
        return StringUtil.getRandomString(5);
    }

    private int getRandomAge() {
        return new Random().nextInt(100);
    }

}
