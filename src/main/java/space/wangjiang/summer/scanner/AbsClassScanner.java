package space.wangjiang.summer.scanner;

import space.wangjiang.summer.util.StringUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by WangJiang on 2018/3/30.
 * 类扫描器
 */
public abstract class AbsClassScanner {

    private String packageName;

    public AbsClassScanner(String packageName) {
        this.packageName = packageName;
    }

    public List<Class<?>> getClassList() {
        List<Class<?>> classList = new ArrayList<>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url == null) {
                    continue;
                }
                // 获取协议名(分为file与jar)
                String protocol = url.getProtocol();
                if (protocol.equalsIgnoreCase("file")) {
                    // 若在class目录中，则执行添加类操作
                    String packagePath = url.getPath().replaceAll("%20", " ");
                    addClass(classList, packagePath, packageName);
                } else if (protocol.equalsIgnoreCase("jar")) {
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = jarURLConnection.getJarFile();
                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                    while (jarEntries.hasMoreElements()) {
                        JarEntry jarEntry = jarEntries.nextElement();
                        String jarEntryName = jarEntry.getName();
                        if (jarEntryName.endsWith(".class")) {
                            // 获取类名
                            String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                            // 执行添加类操作
                            doAddClass(classList, className);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }

    private void addClass(List<Class<?>> classList, String packagePath, String packageName) {
        try {
            File[] files = new File(packagePath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isFile() && file.getName().endsWith(".class") || file.isDirectory();
                }
            });
            if (files == null) {
                return;
            }
            for (File file : files) {
                String fileName = file.getName();
                if (file.isFile()) {
                    //获取类类名
                    String className = fileName.substring(0, fileName.indexOf("."));
                    if (StringUtil.isNotEmpty(packageName)) {
                        className = packageName + "." + className;
                    }
                    //添加类
                    doAddClass(classList, className);
                } else {
                    //获取子包
                    String subPackagePath = fileName;
                    if (StringUtil.isNotEmpty(subPackagePath)) {
                        subPackagePath = packagePath + "/" + subPackagePath;
                    }
                    String subPackageName = fileName;
                    if (StringUtil.isNotEmpty(subPackageName)) {
                        subPackageName = packageName + "." + subPackageName;
                    }
                    //递归调用
                    addClass(classList, subPackagePath, subPackageName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void doAddClass(List<Class<?>> classList, String className) {
        Class<?> clazz = loadClass(className);
        //它的子类实现是否添加该类，这样可以灵活实现，获取所有类，继承自XX的类，带有XX注解的类
        if (accept(clazz)) {
            classList.add(clazz);
        }
    }

    /**
     * 类的过滤器
     * 符合条件的类才加到列表里
     */
    public abstract boolean accept(Class<?> clazz);

    /**
     * 加载类
     */
    private Class<?> loadClass(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className, false, getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return clazz;
    }

    private ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
