package space.wangjiang.summer.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionUtil {

    /**
     * 释放资源
     * Transaction内部的不能调用改方法，需要等事务提交或者回滚之后才可以关闭连接
     */
    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        close(resultSet);
        close(statement);
        closeConnection(connection);
    }

    public static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 事务操作不能随便关闭连接，事务commit或者rollback之后才可以释放Connection资源
     */
    private static void closeConnection(Connection connection) {
        ModelConfig config = ModelConfig.config;
        if (config == null || config.getThreadLocalConnection() == null) {
            //当前连接不是用于事务操作
            close(connection);
        }
    }

}
