package space.wangjiang.summer.model;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by WangJiang on 2017/9/12.
 * 数据连接池
 */
public class DataSourceUtil {

    private static Properties properties = new Properties();
    private static DataSource dataSource = null;

    public static void init(ModelConfig config) {
        properties.put("driverClassName", config.getDriver());
        properties.put("url", config.getUrl());
        properties.put("username", config.getUsername());
        properties.put("password", config.getPassword());
        try {
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException("数据库连接池初始失败", e);
        }
    }

    /**
     * 获取一个新的DataSource
     */
    public static DataSource getNewDataSource(String driver, String url, String username, String password) throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("driverClassName", driver);
        properties.put("url", url);
        properties.put("username", username);
        properties.put("password", password);
        return DruidDataSourceFactory.createDataSource(properties);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 释放资源
     */
    @SuppressWarnings("all")
    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭数据库连接池
     */
    public static void closeDataSource() {
        ((DruidDataSource) dataSource).close();
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                DriverManager.deregisterDriver(drivers.nextElement());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
