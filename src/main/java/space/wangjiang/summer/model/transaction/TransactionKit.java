package space.wangjiang.summer.model.transaction;

import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.config.SummerConfig;
import space.wangjiang.summer.model.ConnectionUtil;
import space.wangjiang.summer.model.ModelConfig;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务处理
 */
public class TransactionKit {

    /**
     * 处理事务，支持嵌套事务
     */
    public static boolean transaction(int transactionLevel, Atom atom) {
        ModelConfig modelConfig = SummerConfig.getModelConfigStatically();
        Connection connection = modelConfig.getThreadLocalConnection();
        if (connection != null) {
            //当前已经有一个事务，嵌套事务
            try {
                if (connection.getTransactionIsolation() < transactionLevel) {
                    connection.setTransactionIsolation(transactionLevel);
                }
                boolean result = atom.run();
                if (result) return true;
                throw new TransactionException("嵌套事务返回false");
            } catch (Exception e) {
                throw new TransactionException("嵌套事务出现异常", e);
            }
        }

        Boolean autoCommit = null;
        try {
            connection = modelConfig.getConnection();
            autoCommit = connection.getAutoCommit();
            modelConfig.setThreadLocalConnection(connection);
            connection.setTransactionIsolation(transactionLevel);
            connection.setAutoCommit(false);
            boolean result = atom.run();
            if (result) {
                Logger.debug("connection commit");
                connection.commit();
            } else {
                //回滚事务
                connection.rollback();
            }
            return result; //try语句中即便存在return，也会先执行finally
        } catch (Exception e) {
            e.printStackTrace();
            //catch所有异常，包括嵌套事务的错误
            if (connection != null) {
                try {
                    connection.rollback();
                    Logger.debug("connection.rollback()");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    //因为连接会回到连接池，一定要恢复到原始状态
                    if (autoCommit != null) {
                        //如果autoCommit是null，说明getAutoCommit()就出错了，此时connection的autoCommit还没有修改为false
                        connection.setAutoCommit(autoCommit);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    //一定要移除掉ThreadLocal的数据库连接，否则会导致内存泄露
                    modelConfig.removeThreadLocalConnection();
                    //所有事务提交或者回滚之后才可以关闭连接
                    ConnectionUtil.close(connection);
                }
            }
        }
    }

}
