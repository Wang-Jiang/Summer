package space.wangjiang.summer.model;

import space.wangjiang.summer.model.db.Dialect;
import space.wangjiang.summer.model.db.MySqlDialect;

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

    public void init(String driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;

        //初始化DataSourceUtil
        DataSourceUtil.init(this);
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
}
