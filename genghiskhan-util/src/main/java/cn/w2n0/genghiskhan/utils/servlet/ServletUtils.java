package cn.w2n0.genghiskhan.utils.servlet;

import cn.w2n0.genghiskhan.utils.convert.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 客户端工具类
 * 
 * @author 无量
 */
public class ServletUtils
{

    /**
     * 获取String参数
     */
    public static String getParameter(String name)
    {
        return getRequest().getParameter(name);
    }
    public static final String KEY1="XMLHttpRequest";
    public static final String KEY2=".json";
    public static final String KEY3=".xml";
    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue)
    {
        return ConvertUtils.toStr(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name)
    {
        return ConvertUtils.toInt(getRequest().getParameter(name));
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue)
    {
        return ConvertUtils.toInt(getRequest().getParameter(name), defaultValue);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest()
    {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse()
    {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession()
    {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes()
    {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 将字符串渲染到客户端
     * 
     * @param response 渲染对象
     * @param string 待渲染的字符串
     * @return null
     */
    public static String renderString(HttpServletResponse response, String string)
    {
        try
        {
            response.setStatus(200);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 是否是Ajax异步请求
     * 
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request)
    {
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf(MediaType.APPLICATION_JSON_VALUE) != -1)
        {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.indexOf(KEY1) != -1)
        {
            return true;
        }

        String uri = request.getRequestURI();
        if (StringUtils.containsIgnoreCase(uri, KEY2)
                ||
                StringUtils.containsIgnoreCase(uri, KEY3)
                )
        {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        if (StringUtils.containsIgnoreCase(ajax, KEY2)
                ||
                StringUtils.containsIgnoreCase(ajax, KEY3)
                )
        {
            return true;
        }
        return false;
    }
}
