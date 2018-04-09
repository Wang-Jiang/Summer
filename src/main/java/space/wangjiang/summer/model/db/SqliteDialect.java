package space.wangjiang.summer.model.db;

/**
 * Created by WangJiang on 2017/10/2.
 */
public class SqliteDialect extends BaseDialect {

    @Override
    public String getAllTableNameSql() {
        return "SELECT name FROM sqlite_master WHERE type='table' order by name;";
    }

    @Override
    public String wrapColumn(String column) {
        return "[" + column + "]";
    }

    @Override
    public String wrapTable(String table) {
        return "[" + table + "]";
    }

    @Override
    public String buildPageSql(String baseSql, long offset, int pageSize) {
        return baseSql + String.format(" LIMIT %s OFFSET %s;", pageSize, offset);
    }

}
