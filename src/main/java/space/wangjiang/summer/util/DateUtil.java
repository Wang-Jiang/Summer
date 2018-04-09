package space.wangjiang.summer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * 时间格式yyyyMMddHHmmss
     */
    public static String getCurrentDateSimple() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date);
    }

    /**
     * 时间格式yyyy/MM/dd HH:mm:ss
     */
    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * 获取本月第一天时间
     * 时间格式yyyy/MM/dd HH:mm:ss
     */
    public static String getThisMonthDate() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/");
        return dateFormat.format(now) + "01 00:00:00";
    }

}
