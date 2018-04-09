package space.wangjiang.summer.model;

import org.junit.Before;
import org.junit.Test;
import space.wangjiang.easylogger.EasyLogger;
import space.wangjiang.summer.common.Logger;
import space.wangjiang.summer.model.db.MySqlDialect;
import space.wangjiang.summer.model.db.SqliteDialect;
import space.wangjiang.summer.util.FileUtil;

import javax.sql.DataSource;
import java.io.File;

/**
 * Created by WangJiang on 2017/9/29.
 */
public class ModelGeneratorTest {

    private ModelGenerator buildModelGenerator(DataSource dataSource) {
        String rootPath = new File("").getAbsolutePath();
        String modelPath = rootPath + "/src/test/java/space/wangjiang/summer/model/";
        String modelPackage = "space.wangjiang.summer.model";

        ModelGenerator generator = new ModelGenerator();
        generator.setDataSource(dataSource);
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
        DataSource dataSource = ModelTestUtil.getDataSource();
        ModelGenerator generator = buildModelGenerator(dataSource);
        generator.generate();
    }

    @Test
    public void readResourceFileTest() {
        ModelGenerator generator = new ModelGenerator();
        Logger.debug(generator.readResourceFile("MappingKit.enjoy"));
    }

}
