package space.wangjiang.summer.test;

import java.util.HashMap;

/**
 * 请求参数
 */
public class Param {

    private HashMap<String, String> data = new HashMap<>();

    public void put(String key, Object value) {
        data.put(key, String.valueOf(value));
    }

    public HashMap<String, String> getData() {
        return data;
    }
}
