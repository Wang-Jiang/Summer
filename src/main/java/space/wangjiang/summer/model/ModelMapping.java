package space.wangjiang.summer.model;

/**
 * Created by WangJiang on 2017/9/8.
 * 数据库的表与模型的映射
 */
public class ModelMapping {

    private String table;
    private String[] primaryKey;  //为了处理联合主键
    private Class<? extends Model> model;

    public ModelMapping(String table, String primaryKey, Class<? extends Model> model) {
        this.table = table;
        this.primaryKey = primaryKey.split(",");
        this.model = model;
    }

    public String getTable() {
        return table;
    }

    public String[] getPrimaryKey() {
        return primaryKey;
    }

    public Class<? extends Model> getModel() {
        return model;
    }
}
