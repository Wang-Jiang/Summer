package space.wangjiang.summer.model.provider;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 提供数据库连接
 */
public interface ConnectionProvider {

    /**
     * 用于初始化数据库驱动、数据库连接池等操作
     */
    void init(String driverClass, String url, String username, String password);

    /**
     * 获取连接
     */
    Connection getConnection() throws SQLException;

    /**
     * Summer停止的时候会调用，用于关闭数据库连接池等操作
     */
    void destroy();

}
