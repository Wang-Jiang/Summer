package space.wangjiang.summer.model;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WangJiang on 2017/9/8.
 * 数据库的表与模型的映射
 */
public class ModelMapping {

    private String table;
    private String[] primaryKey;  //为了处理联合主键
    private Class<? extends Model> model;
    // 字段以及对应Java类型的映射
    private Map<String, Class<?>> columnTypes = new HashMap<>();

    public ModelMapping(String table, String primaryKey, Class<? extends Model> model) {
        this.table = table;
        this.primaryKey = primaryKey.split(",");
        this.model = model;

        buildColumnTypes();
    }

    /**
     * 获取字段以及对于的Java类型
     */
    private void buildColumnTypes() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ModelConfig.config.getConnection();
            statement = connection.prepareStatement(ModelConfig.config.getDialect().getTableMetaDataSql(table));
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                // 直接使用Class.forName来获取类型
                columnTypes.put(metaData.getColumnName(i), Class.forName(metaData.getColumnClassName(i)));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionUtil.close(connection, statement, resultSet);
        }
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

    public Class<?> getColumnType(String column) {
        return columnTypes.get(column);
    }
}
