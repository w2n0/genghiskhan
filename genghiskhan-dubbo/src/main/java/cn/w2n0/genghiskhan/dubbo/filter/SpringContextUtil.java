package cn.w2n0.genghiskhan.dubbo.filter;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;


/**
 * springContextHolder
 * @author 无量
 */
    public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        checkApplicationContext();
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static Object getBean(String name) {
        try {
            checkApplicationContext();
            return applicationContext.getBean(name);
        } catch (Exception x) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        try {
            checkApplicationContext();
            Map beanMaps = applicationContext.getBeansOfType(clazz);
            if (beanMaps != null && !beanMaps.isEmpty()) {
                return (T) beanMaps.values().iterator().next();
            } else {
                return null;
            }
        } catch (Exception x) {
            return null;
        }
    }

    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("applicaitonContext未注入");
        }
    }
}