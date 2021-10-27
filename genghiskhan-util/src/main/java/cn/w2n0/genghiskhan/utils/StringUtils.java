package cn.w2n0.genghiskhan.utils;

import java.util.regex.Pattern;

/**
 * @author 无量
 * date 2021/9/17 23:04
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    static Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
    /**
     * 判断是否是数字类型
     * @param str 字符串
     * @return 如果为空返回false  匹配返回true
     */
    public static boolean isInteger(String str) {
        if (StringUtils.isBlank(str)){
            return false;
        }
        return pattern.matcher(str).matches();
    }
}
