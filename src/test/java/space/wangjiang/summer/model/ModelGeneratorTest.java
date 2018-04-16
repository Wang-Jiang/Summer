package space.wangjiang.summer.model;

import org.junit.Before;
import org.junit.Test;
import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.model.dialect.MySqlDialect;
import space.wangjiang.summer.model.provider.ConnectionProvider;

import javax.sql.DataSource;
import java.io.File;

/**
 * Created by WangJiang on 2017/9/29.
 */
public class ModelGeneratorTest {

    private ModelGenerator buildModelGenerator(ConnectionProvider provider) {
        String rootPath = new File("").getAbsolutePath();
        String modelPath = rootPath + "/src/test/java/space/wangjiang/summer/model/";
        String modelPackage = "space.wangjiang.summer.model";

        ModelGenerator generator = new ModelGenerator();
        generator.setConnectionProvider(provider);
        generator.setModelPath(modelPath);
        generator.setModelBeanPath(modelPath + "bean");
        generator.setModelPackage(modelPackage);
        generator.setModelBeanPackage(modelPackage + ".bean");
        generator.setDialect(new MySqlDialect());
        return generator;
    }

    @Before
    public void init() {
        EasyLogger.showCallMethodAndLine(false);
    }

    @Test
    public void generatorTest() throws Exception {
        ModelGenerator generator = buildModelGenerator(ModelTestUtil.getConnectionProvider());
        generator.generate();
    }

    @Test
    public void readResourceFileTest() {
        ModelGenerator generator = new ModelGenerator();
        Logger.debug(generator.readResourceFile("MappingKit.enjoy"));
    }

}
