package space.wangjiang.summer.util;

import java.io.*;

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

    public static String readFileToString(File file, String encoding) {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader streamReader = new InputStreamReader(new FileInputStream(file), encoding);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    //以下代码修改自Common IO

    public static void write(File file, CharSequence data, String encoding) throws IOException {
        write(file, data, encoding, false);
    }

    public static void write(File file, CharSequence data, String encoding, boolean append) throws IOException {
        String str = data == null ? null : data.toString();
        writeStringToFile(file, str, encoding, append);
    }

    public static void writeStringToFile(File file, String data, String encoding, boolean append) throws IOException {
        try (FileOutputStream out = openOutputStream(file, append)) {
            if (data != null) {
                out.write(data.getBytes(encoding));
            }
        }
    }

    private static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Directory '" + parent + "' could not be created");
            }
        }
        return new FileOutputStream(file, append);
    }

}
