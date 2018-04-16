package space.wangjiang.summer.model.provider;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Druid数据连接池
 */
public class DruidConnectionProvider implements ConnectionProvider {

    private DruidDataSource dataSource = null;
    private Map<String, Object> properties; //Druid的配置

    public DruidConnectionProvider() {
    }

    public DruidConnectionProvider(Map<String, Object> properties) {
        this.properties = properties;
    }

    @Override
    public void init(String driverClass, String url, String username, String password) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, driverClass);
        properties.put(DruidDataSourceFactory.PROP_URL, url);
        properties.put(DruidDataSourceFactory.PROP_USERNAME, username);
        properties.put(DruidDataSourceFactory.PROP_PASSWORD, password);
        if (this.properties != null) {
            properties.putAll(this.properties);
        }
        try {
            dataSource  = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void destroy() {
        dataSource.close();
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                DriverManager.deregisterDriver(drivers.nextElement());
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

}
