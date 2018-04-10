package space.wangjiang.summer.util;

import org.junit.Assert;
import org.junit.Test;
import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.test.BaseTest;

import java.io.File;
import java.io.IOException;

public class FileUtilTest extends BaseTest {

    @Test
    public void getExtNameTest() {
        Assert.assertEquals(".xls", FileUtil.getExtName("text.xls"));
        Assert.assertEquals(".xls", FileUtil.getExtName("test.text.xls"));
        Assert.assertEquals("", FileUtil.getExtName("text"));
//        Assert.assertEquals("", FileUtil.getExtName("/aa/../text"));
    }

    @Test
    public void getSystemTempDirTest() {
        EasyLogger.debug(FileUtil.getSystemTempDir());
    }

    @Test
    public void readTest() {
        File file = new File("D:/test.txt");
        String res = FileUtil.readFileToString(file, "GB2312");
        EasyLogger.debug(res);
    }


    @Test
    public void writeTest() throws IOException {
        String fileName = StringUtil.getRandomString(10);
        String data = "中文测试" + StringUtil.getRandomString(20);
        File tempFile = new File(FileUtil.getSystemTempDir() + fileName);
        FileUtil.write(tempFile, data, "UTF-8");
        EasyLogger.debug(tempFile.getAbsolutePath());
        String res = FileUtil.readFileToString(tempFile, "UTF-8");
        Assert.assertEquals(data, res);
    }

}
