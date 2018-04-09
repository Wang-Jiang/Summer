package space.wangjiang.summer.util;

import org.junit.Assert;
import org.junit.Test;

public class FileUtilTest {

    @Test
    public void getExtNameTest() {
        Assert.assertEquals(".xls", FileUtil.getExtName("text.xls"));
        Assert.assertEquals(".xls", FileUtil.getExtName("test.text.xls"));
        Assert.assertEquals("", FileUtil.getExtName("text"));
//        Assert.assertEquals("", FileUtil.getExtName("/aa/../text"));
    }

}
