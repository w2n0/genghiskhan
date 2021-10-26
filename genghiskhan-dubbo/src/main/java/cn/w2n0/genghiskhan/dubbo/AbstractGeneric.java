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
     * @param version
     * @param methodName
     * @param paramsMap
     * @return
     */
    protected abstract Object invoke(String interfaceName, String version, String methodName, Map<String, Object> paramsMap);

    /**
     * 调用并返回Json
     * @param interfaceName invokeToJson
     * @param version
     * @param methodName
     * @param paramsMap
     * @return
     */
    protected abstract String invokeToJson(String interfaceName, String version, String methodName, Map<String, Object> paramsMap);

    @Override
    public Object exec(String service, String version, String method, Map<String, Object> paramsMap) {
        return invoke(service, version, method, paramsMap);
    }

    @Override
    public String execToJson(String service, String version, String method, Map<String, Object> paramsMap) {
        return invokeToJson(service, version, method, paramsMap);
    }
}
