package space.wangjiang.summer.util;

/**
 * Created by WangJiang on 2016/7/10.
 */
public class FileUtil {

    /**
     * 获取文件后缀名，带有 . 如.png
     * 只能传入文件名称，传入文件路径，没有考虑/xx/../xx的情况，会获取./xx后缀
     */
    public static String getExtName(String fileName) {
        // 获取扩展名
        int pointIndex = fileName.lastIndexOf(".");
        if (pointIndex > -1) {
            return fileName.substring(pointIndex, fileName.length());
        }
        return "";
    }

    /**
     * 获取文件后缀，不要 .
     */
    public static String getExtNameWithoutPoint(String fileName) {
        String extName = getExtName(fileName);
        if (extName.length() == 0) {
            return extName;
        }
        return extName.substring(1, extName.length());
    }

    /**
     * 传入后缀名，判断是否是图片
     */
    public static boolean isImageExt(String extName) {
        if (extName.equalsIgnoreCase(".PNG")
                || extName.equalsIgnoreCase(".JPG")
                || extName.equalsIgnoreCase(".BMP")
                || extName.equalsIgnoreCase(".GIF")
                || extName.equalsIgnoreCase(".JPEG")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是一个图片的名字
     */
    public static boolean isImageName(String fileName) {
        return isImageExt(getExtName(fileName));
    }

    /**
     * 传入后缀获取一个随机的名字
     */
    public static String getRandomFileName(String extName) {
        return DateUtil.getCurrentDateSimple() + StringUtil.getRandomString(32) + extName;
    }

    public static String getFileName(String path) {
        String[] array = path.replaceAll("\\\\", "/").split("/");
        return array[array.length - 1];
    }

    /**
     * 获取系统临时文件目录
     */
    public static String getSystemTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 是否是相对路径
     */
    public static boolean isRelativePath(String path) {
        if (path == null) {
            return false;
        }
        return !isAbsolutePath(path);
    }

    /**
     * 是否是绝对路径
     */
    public static boolean isAbsolutePath(String path) {
        if (path == null) {
            return false;
        }
        return path.startsWith("/") || path.contains(":");
    }

}
