package space.wangjiang.summer.model;

import space.wangjiang.easylogger.json.JsonUtil;
import space.wangjiang.easylogger.json.handler.IJson;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.config.SummerConfig;
import space.wangjiang.summer.model.dialect.Dialect;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * Created by WangJiang on 2017/9/8.
 * Model
 */
public class Model<M extends Model<?>> implements IJson {

    private Map<String, Object> attrs = new HashMap<>();

    /**
     * 被更新的字段
     */
    private Set<String> modifyKeys = null;

    private ModelMapping modelMapping = getModelMapping();

    public Model() {
        super();
    }

    public Model(ModelMapping modelMapping) {
        super();
        this.modelMapping = modelMapping;
    }

    /**
     * 需要注意，params不要直接传入null
     * find("select * from user where name=?", null)
     * 这会导致NPE，同时这个语句也是错误的，应该是"where name is null"
     * 可以传入一个值为null的变量或者(Object) null
     * String name = null;
     * find("select * from user where name=?", name);
     * 或
     * find("select * from user where name=?", (Object) null);
     */
    @SuppressWarnings("unchecked")
    public List<M> find(String sql, Object... params) {
        Logger.debug("sql: " + sql);
        List<M> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            getDialect().fillStatement(statement, params);
            resultSet = statement.executeQuery();
            //由查询的结果构建Model
            ResultSetMetaData metaData = resultSet.getMetaData();
            String[] columnLabels = new String[metaData.getColumnCount()];
            for (int i = 0; i < columnLabels.length; i++) {
                //不能调用getColumnName，这个是数据库的实际字段，如果使用了AS别名，getObject就会出错
                //这个index是从1算的
                columnLabels[i] = metaData.getColumnLabel(i + 1);
            }
            while (resultSet.next()) {
                M model = (M) getClass().newInstance();
                for (String columnLabel : columnLabels) {
                    model.getAttrs().put(columnLabel, resultSet.getObject(columnLabel));
                }
                list.add(model);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionUtil.close(connection, statement, resultSet);
        }
        return list;
    }

    public M findById(Object... id) {
        String sql = getDialect().buildFindByIdSql(modelMapping);
        List<M> result = find(sql, id);
        if (result.size() > 0) return result.get(0);
        return null;
    }

    public M findFirst(String sql, Object... params) {
        List<M> list = find(sql, params);
        if (list.size() > 0) return list.get(0);
        return null;
    }

    /**
     * 保存Model
     */
    public boolean save() {
        List<Object> params = new ArrayList<>();
        String sql = getDialect().buildSaveSql(modelMapping, attrs, params);
        Logger.debug("save:" + sql);
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            //后面的参数表明返回ID
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //填充参数
            getDialect().fillStatement(statement, params);
            int result = statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();

            //处理主键是自增主键
            if (resultSet.next()) {
                for (String primaryKey : getPrimaryKey()) {
                    if (get(primaryKey) == null) {
                        Object value;
                        // mysql中当自增主键是int，但是ResultSetMetaData.getColumnType返回的类型却是Long
                        Class<?> columnType = modelMapping.getColumnType(primaryKey);
                        if (columnType == Integer.class) {
                            value = resultSet.getInt(1);
                        } else if (columnType == Long.class) {
                            value = resultSet.getLong(1);
                        } else {
                            value = resultSet.getObject(1);
                        }
                        getAttrs().put(primaryKey, value);
                    }
                }
            }
            modifyKeys.clear();
            return result >= 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionUtil.close(connection, statement, resultSet);
        }
    }

    public boolean delete() {
        String[] primaryKeys = modelMapping.getPrimaryKey();
        Object[] ids = new Object[primaryKeys.length];
        for (int i = 0; i < primaryKeys.length; i++) {
            ids[i] = attrs.get(primaryKeys[i]);
            if (ids[i] == null) {
                throw new RuntimeException(String.format("Primary key '%s' cannot be NULL.", primaryKeys[i]));
            }
        }
        return deleteById(ids);
    }

    public boolean deleteById(Object... id) {
        String sql = getDialect().buildDeleteByIdSql(modelMapping);
        return executeUpdate(sql, id);
    }

    /**
     * update只更新被修改的字段，即调用过 set() 的字段
     */
    public boolean update() {
        if (modifyKeys == null || modifyKeys.isEmpty()) {
            return false;
        }
        checkPrimaryKeyNotNull();
        List<Object> params = new ArrayList<>();
        String sql = getDialect().buildUpdateSql(modelMapping, attrs, modifyKeys, params);
        modifyKeys.clear();
        return executeUpdate(sql, params.toArray());
    }

    /**
     * 判断主键中是否包含null
     */
    private void checkPrimaryKeyNotNull() {
        String[] primaryKey = getPrimaryKey();
        for (String key : primaryKey) {
            Object value = get(key);
            if (value == null) {
                throw new RuntimeException(String.format("Primary key '%s' cannot be NULL.", key));
            }
        }
    }

    /**
     * 执行update或者delete语句
     * 需要注意当params是null的时候
     * executeUpdate("delete * from user where name=?", null);
     * 这时候并不是指删除name为null的记录，而是params本身是null，因此会NPE，这个问题在find中也存在
     * 但是不准备处理这个问题，一个是这种写法本身就是错的，应该是 "where name is null"
     * 其次如果判断params是否是null，分情况处理会比较乱，那么久干脆就不处理了
     * 同时
     * String name = null;
     * executeUpdate("delete * from user where name=?", name);
     * 这样是不会NPE的
     */
    public boolean executeUpdate(String sql, Object... params) {
        Logger.debug("sql: " + sql);
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            getDialect().fillStatement(statement, params);
            return statement.executeUpdate() >= 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public M setAttr(Map<String, Object> attrs) {
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            this.attrs.put(entry.getKey(), entry.getValue());
        }
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M remove(String attr) {
        attrs.remove(attr);
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M remove(String... attrs) {
        if (attrs != null) {
            for (String attr : attrs) {
                this.attrs.remove(attr);
            }
        }
        return (M) this;
    }

    /**
     * 清空所有值
     */
    @SuppressWarnings("unchecked")
    public M clear() {
        attrs.clear();
        return (M) this;
    }

    /**
     * 清除除attr以外的所有值
     */
    @SuppressWarnings("unchecked")
    public M keep(String attr) {
        Object value = attrs.get(attr);
        attrs.clear();
        attrs.put(attr, value);
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public M keep(String... attrs) {
        Map<String, Object> newAttrs = new HashMap<>(attrs.length);
        for (String attr : attrs) {
            newAttrs.put(attr, this.attrs.get(attr));
        }
        this.attrs = newAttrs;
        return (M) this;
    }

    /**
     * 获取主键
     */
    private String[] getPrimaryKey() {
        return modelMapping.getPrimaryKey();
    }

    //各种基础的赋值取值方法

    /**
     * set会修改modifyKeys，在调用update()方法时，会更新这些字段
     * 如果只是往Model中设置值，并不希望更新到数据库，可以调用 getAttrs().put() 直接修改attrs
     */
    @SuppressWarnings("unchecked")
    public M set(String name, Object value) {
        if (modifyKeys == null) {
            modifyKeys = new HashSet<>();
        }
        modifyKeys.add(name);
        attrs.put(name, value);
        return (M) this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) attrs.get(name);
    }

    public Integer getInt(String name) {
        Number value = (Number) attrs.get(name);
        if (value == null) return null;
        return value.intValue();
    }

    public String getStr(String name) {
        Object value = attrs.get(name);
        if (value == null) return null;
        return value.toString();
    }

    public Long getLong(String name) {
        Number value = (Number) attrs.get(name);
        if (value == null) return null;
        return value.longValue();
    }

    public Boolean getBool(String name) {
        return (Boolean) attrs.get(name);
    }

    public Short getShort(String name) {
        Number value = (Number) attrs.get(name);
        if (value == null) return null;
        return value.shortValue();
    }

    public Double getDouble(String name) {
        Number value = (Number) attrs.get(name);
        if (value == null) return null;
        return value.doubleValue();
    }

    public Float getFloat(String name) {
        Number value = (Number) attrs.get(name);
        if (value == null) return null;
        return value.floatValue();
    }

    public BigDecimal getBigDecimal(String name) {
        return (BigDecimal) attrs.get(name);
    }

    /**
     * 获取所有的属性
     */
    public Map<String, Object> getAttrs() {
        return attrs;
    }

    /**
     * 分页
     *
     * @param pageSize   每页大小
     * @param pageNumber 第几页，从1开始计数
     * @param selectSql  select语句 select id,name
     * @param whereSql   后面的条件 from user where id>10
     * @param params     参数
     */
    public Page<M> page(int pageSize, int pageNumber, String selectSql, String whereSql, Object... params) {
        Dialect dialect = getDialect();
        //先计算总行数
        String selectCountSql = "SELECT COUNT(*) " + whereSql;
        M model = findFirst(selectCountSql, params);
        Long totalRow = model.getLong("COUNT(*)");  //总计数

        int offset = pageSize * (pageNumber - 1);
        String pageSql = dialect.buildPageSql(selectSql + " " + whereSql, offset, pageSize);
        List<M> list = find(pageSql, params);
        //总页数
        long totalPage = totalRow % pageSize == 0 ? totalRow / pageSize : totalRow / pageSize + 1;
        return new Page<>(pageSize, totalRow, totalPage, pageNumber, list);
    }

    private Dialect getDialect() {
        return SummerConfig.getModelConfigStatically().getDialect();
    }

    private Connection getConnection() throws SQLException {
        return SummerConfig.getModelConfigStatically().getConnection();
    }

    /**
     * 最好Model内部保持一个ModelMapping的引用
     */
    public ModelMapping getModelMapping() {
        Map<String, ModelMapping> map = SummerConfig.getModelConfigStatically().getModelMappings();
        for (Map.Entry<String, ModelMapping> entry : map.entrySet()) {
            if (entry.getValue().getModel() == getClass()) {
                return entry.getValue();
            }
        }
        throw new RuntimeException("Cannot find this ModelMapping.Do you add this to ModelConfig.");
    }

    @Override
    public String toJson() {
        return JsonUtil.map2Json(attrs);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            sb.append(entry.getKey())
                    .append(':')
                    .append(entry.getValue())
                    .append(',');
        }
        if (sb.length() > 1) {
            //删除最后一个 , ，需要判断长度>1，防止attrs为空的情况
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Model<?> model = (Model<?>) o;

        return Objects.equals(attrs, model.attrs);
    }

    @Override
    public int hashCode() {
        return attrs != null ? attrs.hashCode() : 0;
    }

}
