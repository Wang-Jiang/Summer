package space.wangjiang.summer.scanner;

import org.junit.Test;

import java.lang.annotation.Target;
import java.util.List;

public class ClassScannerTest {

    private ClassScanner scanner = new ClassScanner();

    @Test
    public void getAllListTest() {
        List<Class<?>> list = scanner.getClassList("com.oreilly.servlet");
        print(list);
    }

    @Test
    public void getClassListByAnnotationTest() {
        print(scanner.getClassListByAnnotation("space", Target.class));
    }

    @Test
    public void getControllerClassListTest() {
        print(scanner.getControllerClassList("space"));
    }

    private void print(List<Class<?>> list) {
        for (Class<?> clazz : list) {
            System.out.println(clazz.getName());
        }
    }

}
