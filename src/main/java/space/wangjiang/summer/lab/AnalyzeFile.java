package space.wangjiang.summer.lab;

import space.wangjiang.summer.aop.Bundle;
import space.wangjiang.summer.aop.Interceptor;

/**
 * 用于解析文件请求
 * 因为请求如果是文件的，需要先解析请求，才能正常获取字段值
 */
public class AnalyzeFile implements Interceptor {

    @Override
    public boolean handle(Bundle bundle) {
        bundle.getController().analyzeMultipartRequest();
        return true;
    }

}
