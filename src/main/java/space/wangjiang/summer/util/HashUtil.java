package space.wangjiang.summer.util;

import java.security.MessageDigest;

/**
 * Created by WangJiang on 2017/9/30.
 * Hash工具类
 */
public class HashUtil {

    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    public static String md5(String srcStr) {
        return hash("MD5", srcStr);
    }

    public static String sha1(String srcStr) {
        return hash("SHA-1", srcStr);
    }

    public static String sha256(String srcStr) {
        return hash("SHA-256", srcStr);
    }

    public static String sha384(String srcStr) {
        return hash("SHA-384", srcStr);
    }

    public static String sha512(String srcStr) {
        return hash("SHA-512", srcStr);
    }

    public static String hash(String algorithm, String srcStr) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(srcStr.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            sb.append(HEX_DIGITS[bytes[i] >> 4 & 15]);
            sb.append(HEX_DIGITS[bytes[i] & 15]);
        }
        return sb.toString();
    }

}
