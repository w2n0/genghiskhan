package cn.w2n0.genghiskhan.dubbo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 泛化调用抽象
 * @author 无量
 */
public abstract class AbstractGeneric<T> implements GenericService {

    protected Map<String, T> referenceCache = new ConcurrentHashMap();


    /**
     * 泛化调用
     * @param interfaceName invoke
     * @param version 版本号
     * @param methodName 方法名
     * @param paramsMap 参数键值对
     * @return 返回结果
     */
    protected abstract Object invoke(String interfaceName, String version, String methodName, Map<String, Object> paramsMap);

    /**
     * 调用并返回Json
     * @param interfaceName invokeToJson
     * @param version 版本号
     * @param methodName 方法名
     * @param paramsMap 参数键值对
     * @return json串
     */
    protected abstract String invokeToJson(String interfaceName, String version, String methodName, Map<String, Object> paramsMap);

    /**
     * 调用并返回对象
     * @param service   服务接口
     * @param version   服务版本
     * @param method    服务方法
     * @param paramsMap 参数
     * @return 返回结果
     */
    @Override
    public Object exec(String service, String version, String method, Map<String, Object> paramsMap) {
        return invoke(service, version, method, paramsMap);
    }


    /**
     * 调用并返回Json
     * @param service   服务接口
     * @param version   服务版本
     * @param method    服务方法
     * @param paramsMap 参数
     * @return json串
     */
    @Override
    public String execToJson(String service, String version, String method, Map<String, Object> paramsMap) {
        return invokeToJson(service, version, method, paramsMap);
    }
}
