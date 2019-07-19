package space.wangjiang.summer.model.dialect;

import space.wangjiang.summer.model.ModelGenerator;
import space.wangjiang.summer.model.ModelMapping;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by WangJiang on 2017/9/27.
 * SQL方言
 * 所有的子类都必须注意字段可能是关键字的情况
 * 不需要考虑主键值的不存在的情况，已经在Model判断过了
 */
public abstract class Dialect {

    /**
     * 获取所有表名的SQL
     * 需要保证结果的每一行第一个字段是表名
     * 这个主要用在ModelGenerator
     */
    public abstract String getAllTableNameSql();

    /**
     * 这个是用于从数据库中获取表结构的语句
     * 需要返回类似于如下语句
     * SELECT * FROM TABLE WHERE 1=2
     * 具体使用参见
     *
     * @see ModelGenerator#getTableColumns(String)
     */
    public abstract String getTableMetaDataSql(String tableName);

    /**
     * 这个是获取分页SQL
     * 在MySql中是 LIMIT ?, ?
     * 在Sqlite中是 LIMIT ? OFFSET ?
     */
    public abstract String buildPageSql(String baseSql, long offset, int pageSize);

    /**
     * 用于构建save的SQL语句，必须构建用于预编译的SQL，例如
     * insert table set(aa,bb,cc) values (?,?,?)
     * 后面的values不能直接填入值，否则会导致SQL注入的问题
     *
     * @param mapping 当前表的配置
     * @param attrs   当前Model的数据
     * @param params  用于预编译的参数，传入的是一个空列表
     */
    public abstract String buildSaveSql(ModelMapping mapping, Map<String, Object> attrs, List<Object> params);

    /**
     * 构建update语句
     *
     * @param mapping    model与数据库的映射
     * @param attrs      model的所有字段
     * @param modifyKeys 被修改的字段
     * @param params     参数
     */
    public abstract String buildUpdateSql(ModelMapping mapping, Map<String, Object> attrs, Set<String> modifyKeys, List<Object> params);

    public abstract String buildFindByIdSql(ModelMapping mapping);

    public abstract String buildDeleteByIdSql(ModelMapping mapping);

    boolean isPrimaryKey(String column, String[] pKeys) {
        for (String pKey : pKeys) {
            if (column.equalsIgnoreCase(pKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 往PreparedStatement中填充值，因为有的数据库对Date类型不太一样，需要单独处理
     */
    public void fillStatement(PreparedStatement statement, List<Object> params) throws SQLException {
        for (int i = 0, size = params.size(); i < size; i++) {
            statement.setObject(i + 1, params.get(i));
        }
    }

    public void fillStatement(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

}
