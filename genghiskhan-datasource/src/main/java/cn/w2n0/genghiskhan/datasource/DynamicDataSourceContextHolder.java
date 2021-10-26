package cn.w2n0.genghiskhan.datasource;

/**
 * 数据源路由
 *
 * @author 无量
 */
public class DynamicDataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXTHOLDER = new ThreadLocal<String>();

    public static void setDataSource(String name) {
        CONTEXTHOLDER.set(name);
    }

    public static String getDataSouce() {
        return CONTEXTHOLDER.get();
    }

    public static void remove(){
        CONTEXTHOLDER.remove();
    }
}
