package cn.icframework.core.utils;

import java.security.MessageDigest;

/**
 * MD5工具类
 * 提供MD5加密相关方法。
 */
public class MD5Util {
    /**
     * 默认构造方法
     */
    public MD5Util() {}

    /**
     * 双层md5 第一层text加密 第二层加盐
     * @param text
     * @param salt
     * @return
     */
    public static String encodeDouble(String text, String salt) {
        return encode(encode(text + salt));
    }

    /**
     * 对文本进行MD5加密
     * @param text 明文
     * @return 加密后的字符串
     */
    public static String encode(String text) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        char[] charArray = text.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }

            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
