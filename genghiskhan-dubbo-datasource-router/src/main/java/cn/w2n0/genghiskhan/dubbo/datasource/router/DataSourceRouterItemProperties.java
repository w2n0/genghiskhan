package cn.w2n0.genghiskhan.dubbo.datasource.router;

import lombok.Data;

/**
 * 路由映射配置
 * @author 无量
 */
@Data
public class DataSourceRouterItemProperties {
    /**
     * 租户
     */
    private String tenant;

    /**
     * 数据源标记
     */
    private String tag;

    /**
     * 数据库源类型
     */
    private String type;
}