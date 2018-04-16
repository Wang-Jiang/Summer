package space.wangjiang.summer.model.provider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 默认的连接提供器，getConnection()每次调用会创建新的连接，不建议使用
 */
public class DefaultConnectionProvider implements ConnectionProvider {

    private String url;
    private String username;
    private String password;

    @Override
    public void init(String driverClass, String url, String username, String password) {
        try {
            Class.forName(driverClass);
            this.url = url;
            this.username = username;
            this.password = password;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public void destroy() {

    }
}
