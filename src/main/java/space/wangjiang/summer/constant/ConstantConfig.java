package space.wangjiang.summer.constant;

import space.wangjiang.summer.common.SummerCommon;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WangJiang on 2017/9/10.
 * Summer框架中的配置
 */
public class ConstantConfig {

    /**
     * 这个是所有的错误编码及对于的页面路径
     */
    private Map<Integer, String> errorPageMap = new HashMap<>();

    private String encoding = "UTF-8";

    /**
     * 基本的上传文件的位置
     */
    private String baseUploadPath = "/upload";

    private String startLogo = SummerCommon.START_LOGO;

    /**
     * 开发模式
     */
    private boolean devMode = true;

    /**
     * 是否显示路由表
     */
    private boolean showRoutes = true;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public boolean isShowRoutes() {
        return showRoutes;
    }

    public void setShowRoutes(boolean showRoutes) {
        this.showRoutes = showRoutes;
    }

    public String getBaseUploadPath() {
        return baseUploadPath;
    }

    public void setBaseUploadPath(String baseUploadPath) {
        this.baseUploadPath = baseUploadPath;
    }

    public String getPage404() {
        return errorPageMap.get(404);
    }

    public void setPage404(String page404) {
        errorPageMap.put(404, page404);
    }

    public String getPage500() {
        return errorPageMap.get(500);
    }

    public void setPage500(String page500) {
        errorPageMap.put(500, page500);
    }

    public void setErrorPage(Integer code, String path) {
        errorPageMap.put(code, path);
    }

    public String getErrorPage(Integer code) {
        return errorPageMap.get(code);
    }

    public String getStartLogo() {
        return startLogo;
    }

    public void setStartLogo(String startLogo) {
        this.startLogo = startLogo;
    }

}
