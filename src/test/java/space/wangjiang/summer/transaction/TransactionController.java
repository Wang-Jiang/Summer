package space.wangjiang.summer.transaction;

import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.aop.After;
import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.model.User;
import space.wangjiang.summer.model.transaction.Transaction;

public class TransactionController extends Controller {

    /**
     * 事务操作
     */
    @Transaction
    @After({TransactionInterceptor.class})
    public void index() {
        int count = User.DAO.getCount();
        setAttr("count", count);
        User user = User.DAO.insertNewUser();
        user.save(); //会导致出错，因此下面代码是执行不到的
        String response = String.format("增加了%s条记录", User.DAO.getCount() - count);
        EasyLogger.debug(response);
        renderText(response);
    }

}
