package space.wangjiang.summer.model;

import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.common.ResourceReader;
import space.wangjiang.summer.model.dialect.Dialect;
import space.wangjiang.summer.model.dialect.MySqlDialect;
import space.wangjiang.summer.model.provider.ConnectionProvider;
import space.wangjiang.summer.util.FileUtil;
import space.wangjiang.summer.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by WangJiang on 2017/9/29.
 * Model辅助工具，生成ModelBean的工具
 * 生成Model、ModelBean和ModelKit
 * 模板文件从resources目录下读取，当打包成jar之后，PathUtil的方法失效，需要使用readResourceFile方法
 */
public class ModelGenerator {

    /**
     * 字段名可能是Java关键字的情况
     */
    private static final List<String> JAVA_KEY_WORDS = new ArrayList<>();

    static {
        JAVA_KEY_WORDS.add("abstract");
        JAVA_KEY_WORDS.add("assert");
        JAVA_KEY_WORDS.add("boolean");
        JAVA_KEY_WORDS.add("break");
        JAVA_KEY_WORDS.add("byte");
        JAVA_KEY_WORDS.add("case");
        JAVA_KEY_WORDS.add("catch");
        JAVA_KEY_WORDS.add("char");
        JAVA_KEY_WORDS.add("class");
        JAVA_KEY_WORDS.add("const");
        JAVA_KEY_WORDS.add("continue");
        JAVA_KEY_WORDS.add("default");
        JAVA_KEY_WORDS.add("do");
        JAVA_KEY_WORDS.add("double");
        JAVA_KEY_WORDS.add("else");
        JAVA_KEY_WORDS.add("enum");
        JAVA_KEY_WORDS.add("extends");
        JAVA_KEY_WORDS.add("final");
        JAVA_KEY_WORDS.add("finally");
        JAVA_KEY_WORDS.add("float");
        JAVA_KEY_WORDS.add("for");
        JAVA_KEY_WORDS.add("goto");
        JAVA_KEY_WORDS.add("if");
        JAVA_KEY_WORDS.add("implements");
        JAVA_KEY_WORDS.add("import");
        JAVA_KEY_WORDS.add("instanceof");
        JAVA_KEY_WORDS.add("int");
        JAVA_KEY_WORDS.add("interface");
        JAVA_KEY_WORDS.add("long");
        JAVA_KEY_WORDS.add("native");
        JAVA_KEY_WORDS.add("new");
        JAVA_KEY_WORDS.add("package");
        JAVA_KEY_WORDS.add("private");
        JAVA_KEY_WORDS.add("protected");
        JAVA_KEY_WORDS.add("public");
        JAVA_KEY_WORDS.add("return");
        JAVA_KEY_WORDS.add("strictfp");
        JAVA_KEY_WORDS.add("short");
        JAVA_KEY_WORDS.add("static");
        JAVA_KEY_WORDS.add("super");
        JAVA_KEY_WORDS.add("switch");
        JAVA_KEY_WORDS.add("synchronized");
        JAVA_KEY_WORDS.add("this");
        JAVA_KEY_WORDS.add("throw");
        JAVA_KEY_WORDS.add("throws");
        JAVA_KEY_WORDS.add("transient");
        JAVA_KEY_WORDS.add("try");
        JAVA_KEY_WORDS.add("void");
        JAVA_KEY_WORDS.add("volatile");
        JAVA_KEY_WORDS.add("while");
    }

    /**
     * model和modelBean的包名
     */
    private String modelPackage;
    private String modelBeanPackage;

    /**
     * model和modelBean的路径
     */
    private String modelPath;
    private String modelBeanPath;

    private ConnectionProvider connectionProvider;
    private Dialect dialect = new MySqlDialect();

    /**
     * 需要在checkConfig之后调用
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initFolder() {
        File modelFolder = new File(modelPath);
        File modelBeanFolder = new File(modelBeanPath);
        if (!modelFolder.exists()) {
            modelFolder.mkdirs();
        }
        if (!modelBeanFolder.exists()) {
            modelBeanFolder.mkdirs();
        }
    }

    /**
     * 检查是否必填的都填写了
     * 同时ModelBean的路径和包名如果没有设置，使用缺省值
     */
    private void checkConfig() {
        if (modelPackage == null) {
            throw new RuntimeException("You need to set a valid value from modelPackage");
        }
        if (modelPath == null) {
            throw new RuntimeException("You need to set a valid value from modelPath");
        }
        if (modelBeanPackage == null) {
            //使用缺省值
            modelBeanPackage = modelPackage + ".bean";
        }
        if (modelBeanPath == null) {
            if (modelPath.endsWith("/") || modelPath.endsWith("\\")) {
                modelBeanPath = modelPath + "bean";
            } else {
                modelBeanPath = modelPath + "/bean";
            }
        }
    }

    public void generate() throws SQLException, IOException {
        checkConfig();
        initFolder();
        List<String> tables = getAllTableNames();
        for (String table : tables) {
            Logger.debug("Processing table: " + table);
            generateModelBean(table);
            generateModel(table);
        }
        generateMappingKit(tables);
        Logger.complete();
    }

    private void generateModelBean(String table) throws SQLException, IOException {
        String modelName = StringUtil.upperCamelCase(table);
        String beanName = StringUtil.upperCamelCase(table + "Bean");

        List<Column> columnList = new ArrayList<>();
        Map<String, String> columns = getTableColumns(table);
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            Column column = new Column(entry.getKey(), entry.getValue());
            columnList.add(column);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("modelBeanPackage", modelBeanPackage);
        data.put("modelName", modelName);
        data.put("beanName", beanName);
        data.put("columnList", columnList);
        Template template = Engine.use().getTemplateByString(readResourceFile("ModelBean.enjoy"));
        String result = template.renderToString(data);
        File beanFile = new File(modelBeanPath + "/" + beanName + ".java");
        FileUtil.write(beanFile, result, "UTF-8");
    }

    private void generateModel(String table) throws IOException {
        String modelName = StringUtil.upperCamelCase(table);
        Map<String, String> data = new HashMap<>();
        data.put("modelPackage", modelPackage);
        data.put("modelBeanPackage", modelBeanPackage);
        data.put("modelName", modelName);
        data.put("beanName", StringUtil.upperCamelCase(table + "Bean"));
        Template template = Engine.use().getTemplateByString(readResourceFile("Model.enjoy"));
        String result = template.renderToString(data);
        File modelFile = new File(modelPath + "/" + modelName + ".java");
        if (!modelFile.exists()) {
            //Model文件已经创建的，就不需要覆盖了
            FileUtil.write(modelFile, result, "UTF-8");
        }
    }

    private void generateMappingKit(List<String> tables) throws SQLException, IOException {
        List<Map<String, String>> mappingList = new LinkedList<>();
        for (String table : tables) {
            Map<String, String> mapping = new HashMap<>();
            mapping.put("table", table);
            mapping.put("primaryKey", getPrimaryKey(getTablePrimaryKey(table)));
            mapping.put("model", StringUtil.upperCamelCase(table));
            mappingList.add(mapping);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("modelPackage", modelPackage);
        data.put("mappingList", mappingList);
        Template template = Engine.use().getTemplateByString(readResourceFile("MappingKit.enjoy"));
        String result = template.renderToString(data);
        File modelMappingFile = new File(modelPath + "/MappingKit.java");
        FileUtil.write(modelMappingFile, result, "UTF-8");
    }

    private List<String> getAllTableNames() throws SQLException {
        List<String> list = new ArrayList<>();
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(dialect.getAllTableNameSql());
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            list.add(resultSet.getString(1));
        }
        ConnectionUtil.close(connection, statement, resultSet);
        return list;
    }

    private String[] getTablePrimaryKey(String tableName) throws SQLException {
        List<String> list = new ArrayList<>();
        Connection connection = getConnection();
        ResultSet resultSet = connection.getMetaData().getPrimaryKeys(null, null, tableName);
        while (resultSet.next()) {
            list.add(resultSet.getString(4));
        }
        ConnectionUtil.close(connection, null, resultSet);
        return list.toArray(new String[list.size()]);
    }

    /**
     * 获取表中的所有字段和对应的类型
     */
    private Map<String, String> getTableColumns(String tableName) throws SQLException {
        Map<String, String> column = new LinkedHashMap<>();
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(dialect.getTableMetaDataSql(tableName));
        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            column.put(metaData.getColumnName(i), metaData.getColumnClassName(i));
        }
        ConnectionUtil.close(connection, statement, resultSet);
        return column;
    }

    /**
     * 将主键从数组转化成字符串
     * [id] --> id
     * [blog_id, user_id] --> blog_id,user_id
     */
    private String getPrimaryKey(String[] primaryKeys) {
        StringBuilder sb = new StringBuilder();
        for (String primaryKey : primaryKeys) {
            sb.append(primaryKey).append(',');
        }
        return StringUtil.deleteLastChar(sb).toString();
    }

    public String readResourceFile(String fileName) {
        return new ResourceReader().read(fileName);
    }

    private Connection getConnection() throws SQLException {
        return connectionProvider.getConnection();
    }

    /**
     * 数据库表的字段
     */
    @SuppressWarnings("unused")
    public static class Column {
        private String name;
        private String type;
        private String upperName;
        private String lowerName;

        Column(String name, String type) {
            this.name = name;
            this.type = type;
            this.upperName = getUpperCamelCase();
            this.lowerName = getLowerCamelCase();
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getUpperName() {
            return upperName;
        }

        public String getLowerName() {
            return lowerName;
        }

        /**
         * 获取字段的驼峰大写法
         */
        private String getUpperCamelCase() {
            return StringUtil.upperCamelCase(name);
        }

        /**
         * 获取字段的驼峰小写法
         */
        private String getLowerCamelCase() {
            String lowerName = StringUtil.lowerCamelCase(name);
            if (JAVA_KEY_WORDS.contains(lowerName)) {
                //防止出现字段首字母小写是关键字
                lowerName = "_" + lowerName;
            }
            return lowerName;
        }

    }

    //set

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public void setModelBeanPackage(String modelBeanPackage) {
        this.modelBeanPackage = modelBeanPackage;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public void setModelBeanPath(String modelBeanPath) {
        this.modelBeanPath = modelBeanPath;
    }

    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

}
