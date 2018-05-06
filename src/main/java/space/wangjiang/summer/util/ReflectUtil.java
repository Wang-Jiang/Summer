package space.wangjiang.summer.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectUtil {

    public static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    public static boolean isPublic(Field field) {
        return Modifier.isPublic(field.getModifiers());
    }

    public static boolean isPrivate(Field field) {
        return Modifier.isPrivate(field.getModifiers());
    }

    public static boolean isProtected(Field field) {
        return Modifier.isProtected(field.getModifiers());
    }

    public static boolean isFinal(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }

}
