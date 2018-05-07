package space.wangjiang.summer.transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.model.*;
import space.wangjiang.summer.model.transaction.Atom;
import space.wangjiang.summer.model.transaction.TransactionKit;
import space.wangjiang.summer.test.BaseTest;
import space.wangjiang.summer.test.Param;

import java.sql.Connection;

public class TransactionTest extends BaseTest {

    @Before
    public void init() {
        ModelConfig config = ModelTestUtil.getModelConfig();
        MappingKit.mapping(config);
        config.addMapping("like", "blog_id, user_id", Like.class);
        EasyLogger.showCallMethodAndLine(false);
        EasyLogger.setTag("TransactionTest");
    }

    @After
    public void stop() {
        ModelConfig.config.destroy();
    }

    /**
     * 事务测试
     */
    @Test
    public void transactionTest() {
        int count = User.DAO.getCount();
        TransactionKit.transaction(Connection.TRANSACTION_READ_COMMITTED, new Atom() {
            @Override
            public boolean run() {
                User user = User.DAO.insertNewUser();
//                user.save(); //会导致出错
                transactionInsert();
                return true;
            }
        });
        EasyLogger.debug(String.format("增加了%s条记录", User.DAO.getCount() - count));
    }

    /**
     * 非事务测试
     */
    @Test
    public void NonTransactionTest() {
        int count = User.DAO.getCount();
        try {
            User.DAO.insertNewUser();
            User user = User.DAO.insertNewUser();
            user.save(); //会导致出错
        } catch (Exception e) {
            e.printStackTrace();
        }
        EasyLogger.debug(String.format("增加了%s条记录", User.DAO.getCount() - count));
    }

    private void transactionInsert() {
        TransactionKit.transaction(Connection.TRANSACTION_READ_UNCOMMITTED, new Atom() {
            @Override
            public boolean run() {
                User user = User.DAO.insertNewUser();
                user.save(); //会导致出错
                return true;
            }
        });
    }

    @Test
    public void transactionRouteTest() {
        get( "http://127.0.0.1:8080/transaction", new Param());
    }

}
