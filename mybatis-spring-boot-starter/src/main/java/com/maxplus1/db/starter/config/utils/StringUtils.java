package com.maxplus1.db.starter.config.utils;

import java.util.Arrays;

public class StringUtils {
    public static String getFirstCamelName(String camelName) {
        // 初始化过滤的字符 start
        char[] fiterCharArray = "abcdefghigklmnopqrstuvwxyz.0123456789".toCharArray();
        Arrays.sort(fiterCharArray);
        // 初始化过滤的字符 end

        char[] ch = new char[camelName.length()];

        for (int i = 0, j = 0; i < camelName.length(); i++, j++) {
            if (Arrays.binarySearch(fiterCharArray, camelName.charAt(i)) > -1) {
                // 在声明的过滤字符数组里面的字符直接赋值
                ch[j] = (char) (camelName.charAt(i));
            } else {
                //匹配到大写字母，退出
                break;
            }
        }
        return new String(ch).trim();
    }
}
