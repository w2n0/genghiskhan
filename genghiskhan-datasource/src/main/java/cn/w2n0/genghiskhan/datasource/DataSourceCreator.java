package cn.w2n0.genghiskhan.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.wall.WallFilter;
import cn.w2n0.genghiskhan.utils.security.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Properties;

@Component
@Slf4j

/**
 * 数据源生成器
 * @author 无量
 * @date 2021/10/20 14:24
 */
public class DataSourceCreator {
    private final String SECRETKEY = "erp21202wms2oms2";

    @Autowired(required = false)
    private WallFilter wallFilter;

    /**
     * druid 连接池创建数据源
     *
     * @param itemProperties
     * @return
     */
    public DataSource create(DataSourceItemProperties itemProperties) {
        DruidDataSource ds = null;
        Properties props = new Properties();
        String password = itemProperties.getPassword();
        password = password.length() > 20 ?
                AesUtil.aesDecode(SECRETKEY, itemProperties.getPassword()) : password;
        props.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, itemProperties.getDriverClassName());
        props.put(DruidDataSourceFactory.PROP_URL, itemProperties.getUrl());
        props.put(DruidDataSourceFactory.PROP_USERNAME, itemProperties.getUsername());
        props.put(DruidDataSourceFactory.PROP_PASSWORD, password);
        props.put(DruidDataSourceFactory.PROP_INITIALSIZE, String.valueOf(itemProperties.getInitialSize()));
        props.put(DruidDataSourceFactory.PROP_MAXACTIVE, String.valueOf(itemProperties.getMaxActive()));
        props.put(DruidDataSourceFactory.PROP_MINIDLE, String.valueOf(itemProperties.getMinIdle()));
        props.put(DruidDataSourceFactory.PROP_MAXWAIT, String.valueOf(itemProperties.getMaxWait()));
        props.put(DruidDataSourceFactory.PROP_VALIDATIONQUERY, "select 1");
        props.put("druid.timeBetweenLogStatsMillis", "30000");
        try {
            ds = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            log.error("DynamicDataSourceAutoConfiguration.create", e);
        }
        return ds;
    }


}
