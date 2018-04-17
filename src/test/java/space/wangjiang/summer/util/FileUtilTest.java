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

}
