package space.wangjiang.summer.model.dialect;

import space.wangjiang.summer.model.ModelMapping;
import space.wangjiang.summer.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by WangJiang on 2018/3/27.
 * 因为很多数据库的语句大同小异，比如MySql和Sqlite
 */
public abstract class BaseDialect extends Dialect {

    /**
     * 包裹字段，在Mysql中是``，在SqlServer、Sqlite则是[]
     */
    public abstract String wrapColumn(String column);

    /**
     * 必须使用包裹过的table，防止出现table是关键字的情况
     */
    public abstract String wrapTable(String table);


    /**
     * 这个是用于从数据库中获取表结构的语句
     * 需要返回类似于如下语句
     * SELECT * FROM TABLE WHERE 1=2
     */
    @Override
    public String getTableMetaDataSql(String tableName) {
        return String.format("SELECT * FROM %s WHERE 1=2", wrapTable(tableName));
    }

    /**
     * 这个是用于update的
     * 返回 id1=? AND id2=?
     * 除了构建上面的SQL之外，还需要在参数列表加上主键的值
     * 因此这个方法只能在基础的SQL构建完成之后，才能调用
     */
    private String buildPrimaryKeySql(ModelMapping mapping, Map<String, Object> attrs, List<Object> params) {
        StringBuilder sb = new StringBuilder();
        String[] primaryKey = mapping.getPrimaryKey();
        for (String key : primaryKey) {
            sb.append(wrapColumn(key)).append("=? AND ");
            params.add(attrs.get(key));
        }
        return sb.toString().substring(0, sb.lastIndexOf(" AND "));
    }

    /**
     * 返回 id1=? AND id2=?
     * 用于delete和findById
     */
    private String buildPrimaryKeySql(ModelMapping mapping) {
        StringBuilder sb = new StringBuilder();
        String[] primaryKey = mapping.getPrimaryKey();
        for (String key : primaryKey) {
            sb.append(wrapColumn(key)).append("=? AND ");
        }
        return sb.toString().substring(0, sb.lastIndexOf(" AND "));
    }

    @Override
    public String buildSaveSql(ModelMapping mapping, Map<String, Object> attrs, List<Object> para) {
        StringBuilder keySb = new StringBuilder();
        StringBuilder valueSb = new StringBuilder();
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            keySb.append(wrapColumn(entry.getKey())).append(',');
            valueSb.append("?,");
            para.add(entry.getValue());
        }
        //删除最后一个 ,
        StringUtil.deleteLastChar(keySb);
        StringUtil.deleteLastChar(valueSb);
        return String.format("INSERT INTO %s (%s) VALUES (%s);", wrapTable(mapping.getTable()), keySb, valueSb);
    }

    @Override
    public String buildUpdateSql(ModelMapping mapping, Map<String, Object> attrs, Set<String> modifyKeys, List<Object> params) {
        Map<String, Object> modifyAttrs = new HashMap<>();
        for (String modifyKey : modifyKeys) {
            modifyAttrs.put(modifyKey, attrs.get(modifyKey));
        }
        StringBuilder sb = new StringBuilder();
        // 只更新被修改的字段
        for (Map.Entry<String, Object> entry : modifyAttrs.entrySet()) {
            sb.append(wrapColumn(entry.getKey())).append("=?");
            sb.append(',');
            params.add(entry.getValue());
        }
        //删除最后一个 ,
        StringUtil.deleteLastChar(sb);
        String primaryKeySql = buildPrimaryKeySql(mapping, attrs, params);
        return String.format("UPDATE %s SET %s WHERE %s", wrapTable(mapping.getTable()), sb, primaryKeySql);
    }

    @Override
    public String buildFindByIdSql(ModelMapping mapping) {
        String primaryKeySql = buildPrimaryKeySql(mapping);
        return String.format("SELECT * FROM %s WHERE %s;", wrapTable(mapping.getTable()), primaryKeySql);
    }

    @Override
    public String buildDeleteByIdSql(ModelMapping mapping) {
        String primaryKeySql = buildPrimaryKeySql(mapping);
        return String.format("DELETE FROM %s WHERE %s;", wrapTable(mapping.getTable()), primaryKeySql);
    }

}
