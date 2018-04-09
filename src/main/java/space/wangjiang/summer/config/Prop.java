package space.wangjiang.summer.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by WangJiang on 2017/9/11.
 * Properties的封装
 */
public class Prop {

    private Properties properties = null;

    public Prop(String fileName) {
        this(fileName, SummerConfig.config.getConstantConfig().getEncoding());
    }

    public Prop(String fileName, String encoding) {
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new IllegalArgumentException("文件没有找到 " + fileName);
            }
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
        } catch (IOException e) {
            throw new RuntimeException("无法加载文件", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getStr(String key) {
        return properties.getProperty(key);
    }

    public Integer getInt(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return null;
    }

    public Long getLong(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Long.parseLong(value);
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Boolean.getBoolean(value);
        }
        return null;
    }

    public Double getDouble(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Double.parseDouble(value);
        }
        return null;
    }

}
