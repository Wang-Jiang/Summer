package space.wangjiang.summer.constant;

import space.wangjiang.summer.common.SummerCommon;
import space.wangjiang.summer.config.SummerConfig;
import space.wangjiang.summer.util.FileUtil;

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

    //上传文件相关的配置

    /**
     * 基本的上传文件的位置，该路径是绝对路径
     *
     * @see #setBaseUploadPath
     */
    private String baseUploadPath;

    /**
     * 上传文件的缓冲区大小
     */
    private int uploadSizeThreshold = 4 * 1024;

    /**
     * 上传的单个文件最大值
     */
    private long uploadFileSizeMax = 4L * 1024 * 1024;

    /**
     * 上传文件的临时文件位置
     */
    private String uploadTempFileDir = FileUtil.getSystemTempDir();

    //上传文件相关的配置

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

    /**
     * 设置保存文件的基础路径
     * 支持web的相对路径(不要以 "/" 开头)
     * 支持绝对路径，例如D://upload，或者/home/upload
     */
    public void setBaseUploadPath(String baseUploadPath) {
        if (baseUploadPath == null) {
            throw new RuntimeException("baseUploadPath can not be null");
        }
        if (FileUtil.isRelativePath(baseUploadPath)) {
            baseUploadPath = SummerConfig.config.getServletContext().getRealPath("/" + baseUploadPath);
        }
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

    public void setUploadSizeThreshold(int uploadSizeThreshold) {
        this.uploadSizeThreshold = uploadSizeThreshold;
    }

    public int getUploadSizeThreshold() {
        return uploadSizeThreshold;
    }

    public String getUploadTempFileDir() {
        return uploadTempFileDir;
    }

    public void setUploadTempFileDir(String uploadTempFileDir) {
        this.uploadTempFileDir = uploadTempFileDir;
    }

    public long getUploadFileSizeMax() {
        return uploadFileSizeMax;
    }

    public void setUploadFileSizeMax(long uploadFileSizeMax) {
        this.uploadFileSizeMax = uploadFileSizeMax;
    }
}
