package space.wangjiang.summer.transaction;

import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.aop.Bundle;
import space.wangjiang.summer.aop.Interceptor;
import space.wangjiang.summer.controller.Controller;
import space.wangjiang.summer.model.User;

public class TransactionInterceptor implements Interceptor {

    @Override
    public boolean handle(Bundle bundle) {
        Controller controller = bundle.getController();
        int count = controller.getAttr("count");
        String response = String.format("增加了%s条记录", User.DAO.getCount() - count);
        EasyLogger.debug(response);
        return true;
    }
}
