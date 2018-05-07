package space.wangjiang.summer.model;

import space.wangjiang.summer.config.Prop;
import space.wangjiang.summer.model.dialect.MySqlDialect;
import space.wangjiang.summer.model.dialect.SqliteDialect;
import space.wangjiang.summer.model.provider.ConnectionProvider;
import space.wangjiang.summer.model.provider.DefaultConnectionProvider;
import space.wangjiang.summer.model.provider.DruidConnectionProvider;

/**
 * 测试统一管理Model
 */
public class ModelTestUtil {

    public static ModelConfig getModelConfig() {
        ModelConfig config = new ModelConfig();
        configMySql(config);
        config.setConnectionProvider(new DruidConnectionProvider());
//        config.setConnectionProvider(new DefaultConnectionProvider());
        return config;
    }

    public static void configSqlite(ModelConfig config) {
        Prop prop = new Prop("sqlite-config.properties", "UTF-8");
        config.init(prop.getStr("db.driver"),
                prop.getStr("db.jdbcUrl"),
                prop.getStr("db.user"),
                prop.getStr("db.password"));
        config.setDialect(new SqliteDialect());
    }

    public static void configMySql(ModelConfig config) {
        Prop prop = new Prop("mysql-config.properties", "UTF-8");
        config.init(prop.getStr("db.driver"),
                prop.getStr("db.jdbcUrl"),
                prop.getStr("db.user"),
                prop.getStr("db.password"));
        config.setDialect(new MySqlDialect());
    }

    public static ConnectionProvider getConnectionProvider() {
        Prop prop = new Prop("mysql-config.properties", "UTF-8");
        ConnectionProvider provider = new DefaultConnectionProvider();
        provider.init(prop.getStr("db.driver"),
                prop.getStr("db.jdbcUrl"),
                prop.getStr("db.user"),
                prop.getStr("db.password"));
        return provider;
    }

}
