package cn.w2n0.genghiskhan.datasource;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置
 * @author 无量
 * @date 2021/10/20 14:24
 */
@Configuration
@EnableConfigurationProperties({DataSourceProperties.class})
@ConditionalOnProperty("genghiskhan.datasource.enable")
@Slf4j
public class DynamicDataSourceAutoConfiguration {
    @Autowired
    private DataSourceProperties dataSourceProperties;
    @Autowired
    private DataSourceCreator dataSourceCreator;

    private final String DRUIDWEBURI="/druid/js/common.js";

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource() {
        DynamicDataSource dataSource = new DynamicDataSource();
        Map<Object, Object> resolvedDataSources = new HashMap<>(10);
        for (DataSourceItemProperties itemProperties : dataSourceProperties.getItems()) {
            resolvedDataSources.put(itemProperties.getTag(),
                    dataSourceCreator.create(itemProperties)
            );
        }
        dataSource.setTargetDataSources(resolvedDataSources);
        return dataSource;
    }




    @Bean(name = "wallFilter")
    public WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;
    }

    @Bean(name = "wallConfig")
    public WallConfig wallConfig() {
        WallConfig config = new WallConfig();
        config.setMultiStatementAllow(true);
        config.setFunctionCheck(false);
        config.setStrictSyntaxCheck(false);
        config.setNoneBaseStatementAllow(true);
        return config;
    }

    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(
                new StatViewServlet(), "/druid/*");
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1,10.1.1.1,210.12.66.240");
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "admin");
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.css,/druid/*");

        //创建filter进行过滤
        Filter filter = new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                HttpServletRequest httpServletRequest = (HttpServletRequest) request;
                chain.doFilter(request, response);

                if (httpServletRequest.getRequestURI().contains(DRUIDWEBURI)) {

                    // 获取common文件内容
                    String text = Utils.readFromResource("support/http/resources/js/common.js");
                    // 正则表达式删除 <footer class="footer"> 与 </footer> 之间的内容，包括footer本身
                    text = text.replaceAll("<footer class=\"footer\">[^%]*</footer>", "");
                    // 将新内容返回至前台页面
                    response.getWriter().write(text);
                }
            }

            @Override
            public void destroy() {
            }
        };

        filterRegistrationBean.setFilter(filter);
        return filterRegistrationBean;
    }
}
