package space.wangjiang.summer.test;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import space.wangjiang.easylogger.EasyLogger;

import java.io.IOException;

public class BaseTest {

    static {
        EasyLogger.showCallMethodAndLine(false);
    }

    public Connection init(String url, Param param) {
        Connection connection = Jsoup.connect(url);
        if (param != null) {
            connection.data(param.getData());
        }
        connection.ignoreContentType(true);
        return connection;
    }

    public String post(String url, Param param) {
        Connection connection = init(url, param);
        connection.method(Connection.Method.POST);
        return execute(connection);
    }

    public String get(String url, Param param) {
        Connection connection = init(url, param);
        connection.method(Connection.Method.GET);
        return execute(connection);
    }

    public String execute(Connection connection) {
        try {
            Connection.Response response = connection.execute();
            EasyLogger.debug(response.body());
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void print(Object object) {
        EasyLogger.json(object);
    }

    private void setCookie(Connection connection, String key, Object value) {
        if (value == null) {
            value = "";
        }
        connection.cookie(key, String.valueOf(value));
    }

}
