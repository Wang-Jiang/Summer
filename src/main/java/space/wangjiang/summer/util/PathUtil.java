package space.wangjiang.summer.util;

import java.io.File;

public class PathUtil {

    private static String webRootPath;
    private static String rootClassPath;

    public static String getRootClassPath() {
        if (rootClassPath == null) {
            try {
                String path = PathUtil.class.getClassLoader().getResource("").toURI().getPath();
                rootClassPath = new File(path).getAbsolutePath();
            } catch (Exception e) {
                String path = PathUtil.class.getClassLoader().getResource("").getPath();
                rootClassPath = new File(path).getAbsolutePath();
            }
        }
        return rootClassPath;
    }

    public static String getWebRootPath() {
        if (webRootPath == null) {
            try {
                String path = PathUtil.class.getResource("/").toURI().getPath();
                webRootPath = new File(path).getParentFile().getParentFile().getCanonicalPath();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return webRootPath;
    }

    public static void setWebRootPath(String webRootPath) {
        if (webRootPath == null)
            return;

        if (webRootPath.endsWith(File.separator))
            webRootPath = webRootPath.substring(0, webRootPath.length() - 1);
        PathUtil.webRootPath = webRootPath;
    }

}