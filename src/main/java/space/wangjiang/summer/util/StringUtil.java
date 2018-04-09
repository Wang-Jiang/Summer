package space.wangjiang.summer.util;

import java.util.Random;

public class StringUtil {

    private static final Random RANDOM = new Random();

    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] = (char) (arr[0] + 32);
            return new String(arr);
        } else {
            return str;
        }
    }

    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] = (char) (arr[0] - 32);
            return new String(arr);
        } else {
            return str;
        }
    }

    /**
     * 驼峰小写法
     * StrName --> strName
     * str_name --> strName
     */
    public static String lowerCamelCase(String name) {
        if (name == null) {
            return null;
        }
        return firstCharToLowerCase(upperCamelCase(name));
    }

    /**
     * 驼峰大写法
     * str_name --> StrName
     */
    public static String upperCamelCase(String name) {
        if (name == null) {
            return null;
        }
        name = firstCharToUpperCase(name);
        char[] array = name.toCharArray();
        for (int i = 0; i < array.length; i++) {
            //发现_，将后面的字符改为大写
            if (array[i] == '_' && i < array.length - 1) {
                array[i + 1] = Character.toUpperCase(array[i + 1]);
            }
        }
        String upperCamelCaseName = new String(array);
        return upperCamelCaseName.replace("_", "");
    }

    /**
     * 剪短文本长度
     *
     * @param text   原始字符串
     * @param length 剪短的长度
     * @return 剪短之后的字符串
     */
    public static String cutString(String text, int length) {
        if (text == null) {
            return null;
        }
        if (text.length() > length) {
            text = text.substring(0, length) + "...";
        }
        return text;
    }

    /**
     * 获取指定长度的字符串
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = RANDOM.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否是数字
     */
//    @Deprecated
//    public static boolean isNumeric(String str) {
//        return str.matches("\\d*");
//    }

    /**
     * 返回length位随机数字
     * 返回的不一定是有效数字，有可能是00214之类的0开头的数字
     *
     * @param length 长度
     * @return 随机的数字字符串
     */
    public static String getRandomNumber(int length) {
        int[] num = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(num[RANDOM.nextInt(10)]);
        }
        return sb.toString();
    }

    /**
     * 返回有效的随机数字
     * 第一个不为0
     *
     * @param length 长度
     * @return 随机的数字字符串
     */
    public static String getValidRandomNumber(int length) {
        int max = (int) Math.pow(10, length) - 1;   //99**99
        int min = (int) Math.pow(10, length - 1);       //10**00
        int res = RANDOM.nextInt(max) % (max - min + 1) + min;
        return String.valueOf(res);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isAllEmpty(CharSequence... cs) {
        if (cs == null) {
            return true;
        }
        for (CharSequence sequence : cs) {
            if (isNotEmpty(sequence)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是纯数字的，不包括空格，小数点，+-号
     */
    public static boolean isNumeric(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        } else {
            int sz = cs.length();
            for (int i = 0; i < sz; ++i) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 删除最后一个字符
     */
    public static StringBuilder deleteLastChar(StringBuilder sb) {
        if (sb.length() == 0) {
            return sb;
        }
        return sb.deleteCharAt(sb.length() - 1);
    }

}
