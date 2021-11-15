package cn.w2n0.genghiskhan.utils.http;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @author 无量
 * date: 2021/3/22 14:16
 */
public class HttpParamsUtils {
    /**
     * request to map
     *
     * @param request HttpServletRequest
     * @return map
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

    /**
     * request to map
     *
     * @param request HttpServletRequest
     * @return map
     */
    public static Map<String, Object> requestParamsToMap(ServerHttpRequest request) {
        Map<String, Object> param = new HashMap<>(10);
        String queryParam = request.getURI().getQuery();
        if (StringUtils.isBlank(queryParam)) {
            return param;
        }
        String[] params = queryParam.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                String value = p[1].replaceAll("%", "%25");
                try {
                    param.put(StringUtils.trim(p[0]), java.net.URLDecoder.decode(value, "UTF-8").trim());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return param;
    }
}
