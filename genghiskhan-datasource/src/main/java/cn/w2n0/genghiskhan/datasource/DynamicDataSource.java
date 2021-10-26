package cn.w2n0.genghiskhan.datasource;

/**
 * 数据源设置
 * @author 无量
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSouce();
    }
}
