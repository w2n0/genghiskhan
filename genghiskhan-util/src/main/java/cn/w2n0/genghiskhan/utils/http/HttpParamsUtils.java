package cn.w2n0.genghiskhan.utils.http;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 无量
 * @date: 2021/3/22 14:16
 */
public class HttpParamsUtils    {
    /**
     * request to map
     *
     * @param request
     * @return
     */
    public static Map<String, Object> requestParamsToMap(HttpServletRequest request) {
        Map<String, Object> param = new HashMap<>(10);
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            if (null != request.getParameter(name)) {
                try {
                    String str = request.getParameter(name).toString();
                    str = str.replaceAll("%", "%25");
                    param.put(StringUtils.trim(name), java.net.URLDecoder.decode(str, "UTF-8").trim());
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }
            }
        }
        return param;
    }

}
