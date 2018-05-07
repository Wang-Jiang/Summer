package space.wangjiang.summer.model;

import space.wangjiang.summer.model.bean.UserBean;
import space.wangjiang.summer.util.StringUtil;

import java.util.Random;

public class User extends UserBean<User> {

    public static final User DAO = new User();

    /**
     * 新建一个user
     */
    public User insertNewUser() {
        User user = new User();
        user.setAge(new Random().nextInt(60));
        user.setUsername(StringUtil.getRandomString(4));
        user.setEmail(user.getUsername() + "@example.com");
        user.save();
        return user;
    }

    /**
     * 获取User数量
     */
    public int getCount() {
        return findFirst("SELECT COUNT(*) FROM user").getInt("COUNT(*)");
    }

}