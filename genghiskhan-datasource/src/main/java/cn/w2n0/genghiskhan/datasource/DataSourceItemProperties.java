package cn.w2n0.genghiskhan.datasource;

import lombok.Data;

import java.util.List;
/**
 * 数据源
 * @author 无量
 * @date 2021/10/20 14:24
 */
@Data
public class DataSourceItemProperties {
    private String tag;
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    /**
     * 初始化时建立物理连接的个数
     */
    private int initialSize;
    /**
     * maxActive 最大连接池数量
     */
    private int maxActive;
    /**
     * 最小连接池数量
     */
    private int minIdle;
    /**
     * 获取连接时最大等待时间，单位毫秒
     */
    private int maxWait;
    private List<String> tenants;
}
