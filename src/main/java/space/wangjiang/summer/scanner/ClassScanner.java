package space.wangjiang.summer.scanner;

import space.wangjiang.summer.controller.Controller;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 使用很简单，传入包名就可以了
 * 但是不要想着输入java，看看JRE的所有类，当你实际运行的时候发现什么都没有
 * ClassScanner获取包下面的所有类是通过ClassLoader获取的，JRE的类都是由BootstrapClassLoader负责加载到JVM的
 * 应用类的加载是AppClassLoader负责的，因此你可以获取到你依赖的第三方jar的类而不能获取JRE的
 * 更多内容参见ClassLoader相关内容
 */
public class ClassScanner {

    public List<Class<?>> getClassList(String packageName) {
        return new AbsClassScanner(packageName) {
            @Override
            public boolean accept(Class<?> clazz) {
                //获取所有的类
                return true;
            }
        }.getClassList();
    }

    /**
     * 获取标记该注解的所有类
     */
    public List<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        return new AbsClassScanner(packageName) {
            @Override
            public boolean accept(Class<?> clazz) {
                return clazz.isAnnotationPresent(annotationClass);
            }
        }.getClassList();
    }

    /**
     * 获取该父类的所有子类列表，不包括父类
     */
    public List<Class<?>> getClassListBySuper(String packageName, Class<?> superClass) {
        return new AbsClassScanner(packageName) {
            @Override
            public boolean accept(Class<?> clazz) {
                return superClass.isAssignableFrom(clazz) && !superClass.equals(clazz);
            }
        }.getClassList();
    }

    /**
     * 获取所有的Controller类
     */
    public List<Class<?>> getControllerClassList(String packageName) {
        return getClassListBySuper(packageName, Controller.class);
    }

}
