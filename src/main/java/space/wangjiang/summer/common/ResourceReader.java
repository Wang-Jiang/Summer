package space.wangjiang.summer.common;

import space.wangjiang.summer.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 用于读取resources目录的文件
 * 这个不能设置为静态工具类，否则getClass不能使用，无法正确读取文件
 */
public class ResourceReader {

    public String read(String fileName) {
        try {
            InputStream is = getClass().getResourceAsStream("/" + fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return StringUtil.deleteLastChar(sb).toString();//删除最后一个换行
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
