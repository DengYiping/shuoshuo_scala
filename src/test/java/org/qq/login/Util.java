package org.qq.login;

/**
 * Created by Scott on 1/13/16.
 */
public class Util {
    @Deprecated public static String getGTK(String str){
        int hash = 5381;
        for (int i = 0, len = str.length(); i < len; ++i) {
            hash += (hash << 5) + (int) (char) str.charAt(i);
        }
        return (hash & 0x7fffffff) + "";
    }
}
