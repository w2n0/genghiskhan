package cn.w2n0.genghiskhan.utils.yml;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * yml管理工具
 *
 * @author 无量
 */
public class YmlUtils {
    private static Map<String, String> result = new HashMap<>();
    private static Pattern p1 = Pattern.compile("\\$\\{.*?\\}");

    /**
     * 读取yml文件中的某个value。
     * 支持解析 yml文件中的 ${} 占位符
     * @param ymlInfo yml内容
     * @param key key
     * @return 值
     */
    public static Object getValue(String ymlInfo,String key){
        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> map = yaml.loadAs(ymlInfo, LinkedHashMap.class);
        String[] keys = key.split("[.]");
        for (int i = 0; i < keys.length; i++) {
            Object value = map.get(keys[i]);
            if (i < keys.length - 1){
                map = (LinkedHashMap<String, Object>) value;
            }else if (value == null){
                throw new RuntimeException("key不存在");
            }else {
                String g;
                String keyChild;
                String v1 = (String)value;
                for(Matcher m = p1.matcher(v1); m.find(); value = v1.replace(g, (String)getValue(ymlInfo,keyChild))) {
                    g = m.group();
                    keyChild = g.replaceAll("\\$\\{", "").replaceAll("\\}", "");
                }
                return value;
            }
        }
        return "";
    }

    /**
     * 获取yml字符串的key值
     * @param value yml 字符串
     * @param keys key
     * @return 键值对
     */
    public static Map<String, String> getYmlByString(String value, String... keys) {
        result = new HashMap<>(10);
        Yaml props = new Yaml();
        Object obj = props.loadAs(value, Map.class);
        Map<String, Object> param = (Map<String, Object>) obj;

        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (keys.length != 0 && !keys[0].equals(key)) {
                continue;
            }
            if (val instanceof Map) {
                foreachYaml(key, (Map<String, Object>) val, 1, keys);
            } else {
                result.put(key, val.toString());
            }
        }
        return result;

    }


    /**
     * 遍历yml文件，获取map集合
     *
     * @param keyStr
     * @param obj
     * @param i
     * @param keys
     * @return
     */
    private static Map<String, String> foreachYaml(String keyStr, Map<String, Object> obj, int i, String... keys) {
        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (keys.length > i && !keys[i].equals(key)) {
                continue;
            }
            String strNew = "";
            if (StringUtils.isNotEmpty(keyStr)) {
                strNew = keyStr + "." + key;
            } else {
                strNew = key;
            }
            if (val instanceof Map) {
                foreachYaml(strNew, (Map<String, Object>) val, ++i, keys);
                i--;
            } else {

                result.put(strNew, val.toString());
            }
        }

        return result;
    }

}
