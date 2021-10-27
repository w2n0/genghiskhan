package cn.w2n0.genghiskhan.dubbo;

import java.util.Map;

/**
 * 通用调用服务
 * @author 无量
 */
public interface GenericService {

    /**
     调用返回原始对象
     * @param service 服务接口
     * @param version 服务版本
     * @param method 服务方法
     * @param paramsMap 参数
     * @return 返回结果
     */
    Object exec(String service, String version, String method, Map<String, Object> paramsMap);


    /**
     调用返回Json对象
     * @param service   服务接口
     * @param version   服务版本
     * @param method    服务方法
     * @param paramsMap 参数
     * @return json串
     */
    String execToJson(String service, String version, String method, Map<String, Object> paramsMap);

}
