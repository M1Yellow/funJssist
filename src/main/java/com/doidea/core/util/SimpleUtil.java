package com.doidea.core.util;

public class SimpleUtil {

    public static final int DEFAULT_KEY = 176283;

    public static void main(String[] args) {
        System.out.println(xor("냲냵냯냾냷냷냲냱", DEFAULT_KEY));
        System.out.println(xor("냒냵냯냾냷냷냲냑", DEFAULT_KEY));
    }

    /**
     * xor加密解密
     *
     * @param str
     * @return
     */
    public static String xor(String str, Integer key) {
        if (key == null) key = DEFAULT_KEY;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] ^ key);
        }
        return new String(chars);
    }
}
