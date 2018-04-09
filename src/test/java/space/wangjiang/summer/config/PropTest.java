package space.wangjiang.summer.config;

import org.junit.Assert;
import org.junit.Test;
import space.wangjiang.summer.config.Prop;

/**
 * Created by WangJiang on 2017/9/11.
 */
public class PropTest {

    @Test
    public void prop() {
        Prop prop = new Prop("test.properties", "UTF-8");
        Assert.assertEquals(prop.getStr("stringKey"), "stringKeyTest");
        Assert.assertEquals(prop.getInt("intKey"), new Integer(36));
        Assert.assertEquals(prop.getLong("longKey"), new Long(1225022000000455L));
        Assert.assertEquals(prop.getDouble("doubleKey"), new Double(36.32522));
        Assert.assertEquals(prop.getBoolean("booleanKey"), false);
    }

}
