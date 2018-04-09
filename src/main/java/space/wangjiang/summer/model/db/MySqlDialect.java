package space.wangjiang.summer.model.db;

/**
 * Created by WangJiang on 2017/9/27.
 */
public class MySqlDialect extends BaseDialect {

    @Override
    public String getAllTableNameSql() {
        return "SHOW TABLES;";
    }

    /**
     * 包裹字段
     */
    @Override
    public String wrapColumn(String column) {
        return "`" + column + "`";
    }

    /**
     * 必须使用包裹过的table，防止出现table是关键字的情况
     */
    @Override
    public String wrapTable(String table) {
        return "`" + table + "`";
    }

    @Override
    public String buildPageSql(String baseSql, long offset, int pageSize) {
        return baseSql + String.format(" LIMIT %s, %s;", offset, pageSize);
    }

}
