package cn.w2n0.genghiskhan.dubbo.datasource.router;

import cn.w2n0.genghiskhan.datasource.DynamicDataSourceContextHolder;
import cn.w2n0.genghiskhan.dubbo.extend.TenantEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
/**
 * 租户对象映射
 * @author 无量
 */
@Component
@EnableConfigurationProperties({DataSourceRouterProperties.class})
public class TenantDatabaseRouter {
    @Autowired
    private DataSourceRouterProperties dataSourceRouterProperties;

    @Value("${datasource-type}")
    private String type;

    @EventListener
    public void placeOrderNotice(TenantEntity tenantEntity) {
        if(StringUtils.isEmpty(type))
        {

            throw new RuntimeException("The data source type is not configured for the current project!");
        }
        DynamicDataSourceContextHolder.remove();
        for (DataSourceRouterItemProperties item : dataSourceRouterProperties.getItems()) {
            if (item.getTenant().equalsIgnoreCase(tenantEntity.getTenantId()) && item.getType().equalsIgnoreCase(type)) {
                DynamicDataSourceContextHolder.setDataSource(item.getTag());
                return;
            }
        }
    }
}
