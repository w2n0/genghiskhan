package cn.w2n0.genghiskhan.datasource;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源
 *
 * @author 无量
 */
public abstract class AbstractRoutingDataSource extends AbstractDataSource implements ApplicationContextAware, InitializingBean {
    private transient ApplicationContext applicationContext;

    private Map<Object, Object> targetDataSources;

    private Object defaultTargetDataSource;
    private boolean lenientFallback = true;
    private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();

    private Map<Object, DataSource> resolvedDataSources = new HashMap<>();

    private DataSource resolvedDefaultDataSource;

    public AbstractRoutingDataSource() {
    }

    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setResolvedDefaultDataSource(DataSource resolvedDefaultDataSource) {
        this.resolvedDefaultDataSource = resolvedDefaultDataSource;
    }

    public DataSource getResolvedDefaultDataSource() {
        return resolvedDefaultDataSource;
    }

    public Map<Object, DataSource> getResolvedDataSources() {
        return resolvedDataSources;
    }

    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        this.defaultTargetDataSource = defaultTargetDataSource;
    }

    private <T> T getBean(Class<T> type) {
        try {

            return applicationContext == null ? null :
                    BeanFactoryUtils.beanOfTypeIncludingAncestors(applicationContext, type, false, false);
        } catch (Exception x) {
            logger.info("No injection configuration!");
        }
        return null;
    }

    public void setLenientFallback(boolean lenientFallback) {
        this.lenientFallback = lenientFallback;
    }

    public void setDataSourceLookup(DataSourceLookup dataSourceLookup) {
        this.dataSourceLookup = (DataSourceLookup) (dataSourceLookup != null ? dataSourceLookup : new JndiDataSourceLookup());
    }

    @Override
    public void afterPropertiesSet() {
        if (this.targetDataSources == null) {
            throw new IllegalArgumentException("Property 'targetDataSources' is required");
        } else {
            this.resolvedDataSources = new HashMap(this.targetDataSources.size());
            this.targetDataSources.forEach((key, value) -> {
                Object lookupKey = this.resolveSpecifiedLookupKey(key);
                DataSource dataSource = this.resolveSpecifiedDataSource(value);
                this.resolvedDataSources.put(lookupKey, dataSource);
            });
            if (this.defaultTargetDataSource != null) {
                this.resolvedDefaultDataSource = this.resolveSpecifiedDataSource(this.defaultTargetDataSource);
            }

        }
    }

    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        return lookupKey;
    }

    protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
        if (dataSource instanceof DataSource) {
            return (DataSource) dataSource;
        } else if (dataSource instanceof String) {
            return this.dataSourceLookup.getDataSource((String) dataSource);
        } else {
            throw new IllegalArgumentException("Illegal data source value - only [javax.sql.DataSource] and String supported: " + dataSource);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.determineTargetDataSource().getConnection(username, password);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return iface.isInstance(this) ? (T) this : this.determineTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || this.determineTargetDataSource().isWrapperFor(iface);
    }

    protected DataSource determineTargetDataSource() {
        Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
        Object lookupKey = this.determineCurrentLookupKey();
        DataSource dataSource = (DataSource) this.resolvedDataSources.get(lookupKey);
        boolean success=dataSource == null && (this.lenientFallback || lookupKey == null);
        if (success) {
            dataSource = this.resolvedDefaultDataSource;
        }

        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        } else {
            return dataSource;
        }
    }

    /**
     * 目标数据源
     * @return
     */
    protected abstract Object determineCurrentLookupKey();
}
