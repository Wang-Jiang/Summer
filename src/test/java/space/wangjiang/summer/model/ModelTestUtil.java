package space.wangjiang.summer.model;

import space.wangjiang.summer.config.Prop;
import space.wangjiang.summer.model.db.MySqlDialect;
import space.wangjiang.summer.model.db.SqliteDialect;
import space.wangjiang.summer.util.PathUtil;

import javax.sql.DataSource;
import java.io.File;

/**
 * 测试统一管理Model
 */
public class ModelTestUtil {

    public static ModelConfig getModelConfig() {
        ModelConfig config = new ModelConfig();
        configMySql(config);
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

    public static DataSource getDataSource() {
        try {
            DataSourceUtil.init(getModelConfig());
            return DataSourceUtil.getDataSource();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
