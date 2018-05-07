package space.wangjiang.summer.model;

import space.wangjiang.summer.model.dialect.Dialect;
import space.wangjiang.summer.model.dialect.MySqlDialect;
import space.wangjiang.summer.model.provider.ConnectionProvider;
import space.wangjiang.summer.model.provider.DefaultConnectionProvider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WangJiang on 2017/9/8.
 * 数据库配置
 */
public class ModelConfig {

    private String driver = null;   //TODO 可以缺省不写
    private String url = null;
    private String username = null;
    private String password = null;
    private Dialect dialect = new MySqlDialect();   //默认方言是mysql
    private ConnectionProvider connectionProvider = new DefaultConnectionProvider();  //连接提供器

    /**
     * 用于实现数据库事务
     * getConnection方法会优先从threadLocal中获取连接，如果没有才会从connectionProvider中获取
     * 参见transaction相关代码
     */
    private final ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    /**
     * 这个是为了解决在非Web环境下使用Model功能
     * SummerConfig中存在一个getModelConfigStatically的静态方法
     * 当SummerConfig不存在实例的时候(即非Web环境)，直接调用这里的config
     * 之所以这样设计，主要是考虑到，如果在Web环境下，手动new ModelConfig(Web环境ModelConfig是由SummerConfig实例化的)
     * 会导致SummerConfig中的ModelConfig和这里的config不是同一个实例，可能会导致错误的用法
     * 增加getModelConfigStatically方法，优先使用SummerConfig的ModelConfig
     * 这样可以实现在非Web环境下，正常使用Model，在Web环境下，也不会因为手动new ModelConfig而出现的问题
     */
    public static ModelConfig config = null;

    //整个表的映射
    private Map<String, ModelMapping> modelMappings = new HashMap<>();

    public ModelConfig() {
        config = this;
    }

    /**
     * 需要手动调用初始化
     */
    public void init(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;

        initConnectionProvider();
    }

    private void initConnectionProvider() {
        connectionProvider.init(driver, url, username, password);
    }

    /**
     * primaryKey前后可能会有空格 " id  "、"user_id,  blog_id "
     * 需要注意SQL是支持带有空格的字段，使用的时候需要用``(MySql)包裹起来，但是说实话这个特性太诡异了
     * 如果字段带有空格的话，ModelGenerator生成的代码就会有问题
     * 实在是很难理解为什么SQL设计的时候要支持空格
     * 所以Summer不支持任何带有空格的字段
     */
    public void addMapping(String table, String primaryKey, Class<? extends Model> model) {
        primaryKey = primaryKey.replace(" ", ""); //直接去掉所有的空格
        modelMappings.put(table, new ModelMapping(table, primaryKey, model));
    }

    //getter

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, ModelMapping> getModelMappings() {
        return modelMappings;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    /**
     * 当设置完ConnectionProvider的时候，需要调init()初始化
     * 因为有两种情况调用顺序
     * ModelConfig.init();
     * ModelConfig.setConnectionProvider(); //这个时候需要调用ConnectionProvider的init()方法
     * <p>
     * ModelConfig.setConnectionProvider(); //覆盖了原始的ConnectionProvider，调用init()方法url、username等都是null
     * ModelConfig.init(); //重复ConnectionProvider的init()方法
     */
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        initConnectionProvider();
    }

    public ConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    /**
     * 获取数据库连接，如果threadLocal存在直接返回，用于支持数据库实务操作
     */
    public Connection getConnection() throws SQLException {
        Connection connection = threadLocal.get();
        if (connection != null) return connection;
        return connectionProvider.getConnection();
    }

    public void destroy() {
        connectionProvider.destroy();
    }

    /**
     * 用于支持数据库事务操作
     */
    public void setThreadLocalConnection(Connection connection) {
        threadLocal.set(connection);
    }

    public Connection getThreadLocalConnection() {
        return threadLocal.get();
    }

    /**
     * 当事务提交或者回滚完成之后，需要及时移除，否则会内存泄露
     */
    public void removeThreadLocalConnection() {
        threadLocal.remove();
    }

}
