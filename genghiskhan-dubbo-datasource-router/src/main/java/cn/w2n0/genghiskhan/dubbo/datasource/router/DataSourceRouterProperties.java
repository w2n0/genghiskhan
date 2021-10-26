package cn.w2n0.genghiskhan.dubbo.datasource.router;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 路由映射配置
 * @author 无量
 */
@ConfigurationProperties(prefix = "datasource.router")
@Data
public class DataSourceRouterProperties {
    @NotNull
    private List<DataSourceRouterItemProperties> items;
}
