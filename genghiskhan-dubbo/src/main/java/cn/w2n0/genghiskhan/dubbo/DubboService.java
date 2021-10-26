package cn.w2n0.genghiskhan.dubbo;

import com.alibaba.fastjson.JSONObject;
import cn.w2n0.genghiskhan.entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.*;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.spring.extension.SpringExtensionFactory;
import org.apache.dubbo.config.support.Parameter;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * dubbo 调用服务
 * @author 无量
 */
@SuppressWarnings("deprecation")
public class DubboService extends AbstractGeneric<ReferenceConfig> implements ApplicationContextAware, InitializingBean, DisposableBean {
    private transient ApplicationContext applicationContext;

    private ConsumerConfig consumer;

    protected ApplicationConfig application;

    protected ModuleConfig module;

    protected List<RegistryConfig> registries;

    protected MonitorConfig monitor;

    public DubboService() {

        super();

    }

    @Override
    public Object invoke(String interfaceName, String version, String methodName, Map<String, Object> paramsMap) {
        ReferenceConfig reference = getReferenceConfig(interfaceName, version);
        if (null != reference) {
            GenericService genericService = (GenericService) reference.get();
            Object[] paramObject = null;
            if (!CollectionUtils.isEmpty(paramsMap)) {
                paramObject = new Object[1];
                paramObject[0] = paramsMap;
            }
            Object resultParam = null;
            resultParam = genericService.$invoke(methodName, null, paramObject);
            return resultParam;
        }
        return null;
    }

    @Override
    protected String invokeToJson(String interfaceName, String version, String methodName, Map<String, Object> paramsMap) {
        Result rpcResult;
        try {
            Object resultParam = invoke(methodName, version, methodName, paramsMap);
            if (resultParam instanceof Result) {
                rpcResult = (Result) resultParam;
            } else {
                rpcResult = new Result();
                rpcResult.setResult(resultParam);
            }
        } catch (org.apache.dubbo.rpc.RpcException ex) {
            rpcResult = new Result();
            switch (ex.getCode()) {
                case org.apache.dubbo.rpc.RpcException.NETWORK_EXCEPTION:
                    rpcResult.setCode(ErrorCode.NETWORK_EXCEPTION);
                    break;
                case org.apache.dubbo.rpc.RpcException.TIMEOUT_EXCEPTION:
                    rpcResult.setCode(ErrorCode.TIMEOUT_EXCEPTION);
                    break;
                case org.apache.dubbo.rpc.RpcException.SERIALIZATION_EXCEPTION:
                    rpcResult.setCode(ErrorCode.SERIALIZATION_EXCEPTION);
                    break;
                case org.apache.dubbo.rpc.RpcException.FORBIDDEN_EXCEPTION:
                    rpcResult.setCode(ErrorCode.FORBIDDEN_EXCEPTION);
                    break;
                case org.apache.dubbo.rpc.RpcException.BIZ_EXCEPTION:
                    rpcResult.setCode(ErrorCode.BIZ_EXCEPTION);
                    break;
                default:
                    rpcResult.setCode(ErrorCode.UNKNOWN_EXCEPTION);
                    break;
            }
        }
        return JSONObject.toJSONString(rpcResult);
    }

    @SuppressWarnings("AliDeprecation")
    private ReferenceConfig getReferenceConfig(String interfaceName, String version) {
        String referenceKey = interfaceName;
        ReferenceConfig referenceConfig = referenceCache.get(referenceKey);
        if (null == referenceConfig) {
            try {
                referenceConfig = new ReferenceConfig<GenericService>();
                referenceConfig.setRegistries(registries);
                referenceConfig.setConsumer(consumer);
                referenceConfig.setInterface(interfaceName);
                referenceConfig.setTimeout(3000);
                referenceConfig.setRetries(0);
                if (StringUtils.isNotEmpty(version)) {
                    referenceConfig.setVersion(version);
                }
                referenceConfig.setGeneric("true");
                referenceCache.put(referenceKey, referenceConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return referenceConfig;
    }

    @Parameter(excluded = true)
    public boolean isSingleton() {
        return true;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void afterPropertiesSet() throws Exception {
        if (consumer == null) {
            Map<String, ConsumerConfig> consumerConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ConsumerConfig.class, false, false);
            if (consumerConfigMap != null && consumerConfigMap.size() > 0) {
                ConsumerConfig consumerConfig = null;
                for (ConsumerConfig config : consumerConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (consumerConfig != null) {
                            throw new IllegalStateException("Duplicate consumer configs: " + consumerConfig + " and " + config);
                        }
                        consumerConfig = config;
                    }
                }
                if (consumerConfig != null) {
                    consumer = consumerConfig;
                    consumer.setCheck(false);
                }
            }
        }
        boolean correct=application == null
                && (this.consumer == null || consumer.getApplication() == null);
        if (correct) {
            Map<String, ApplicationConfig> applicationConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ApplicationConfig.class, false, false);
            if (applicationConfigMap != null && applicationConfigMap.size() > 0) {
                ApplicationConfig applicationConfig = null;
                for (ApplicationConfig config : applicationConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (applicationConfig != null) {
                            throw new IllegalStateException("Duplicate application configs: " + applicationConfig + " and " + config);
                        }
                        applicationConfig = config;
                    }
                }
                if (applicationConfig != null) {
                    this.application = applicationConfig;
                    DubboBootstrap.getInstance().application(applicationConfig);
                }
            }
        }
        correct=module == null
                && (consumer == null || consumer.getModule() == null);
        if (correct) {
            Map<String, ModuleConfig> moduleConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ModuleConfig.class, false, false);
            if (moduleConfigMap != null && moduleConfigMap.size() > 0) {
                ModuleConfig moduleConfig = null;
                for (ModuleConfig config : moduleConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (moduleConfig != null) {
                            throw new IllegalStateException("Duplicate module configs: " + moduleConfig + " and " + config);
                        }
                        moduleConfig = config;
                    }
                }
                if (moduleConfig != null) {
                    module = moduleConfig;
                }
            }
        }
        correct=(registries == null || registries.size() == 0)
                && (consumer == null || consumer.getRegistries() == null || consumer.getRegistries().size() == 0)
                && (application == null || application.getRegistries() == null || application.getRegistries().size() == 0);
        if (correct) {
            Map<String, RegistryConfig> registryConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, RegistryConfig.class, false, false);
            if (registryConfigMap != null && registryConfigMap.size() > 0) {
                List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
                for (RegistryConfig config : registryConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        registryConfigs.add(config);
                    }
                }
                if (registryConfigs != null && registryConfigs.size() > 0) {
                    this.registries = registryConfigs;
                }
            }
        }
        correct=monitor == null
                && (consumer == null || consumer.getMonitor() == null)
                && (application == null || application.getMonitor() == null);
        if (correct) {
            Map<String, MonitorConfig> monitorConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, MonitorConfig.class, false, false);
            if (monitorConfigMap != null && monitorConfigMap.size() > 0) {
                MonitorConfig monitorConfig = null;
                for (MonitorConfig config : monitorConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (monitorConfig != null) {
                            throw new IllegalStateException("Duplicate monitor configs: " + monitorConfig + " and " + config);
                        }
                        monitorConfig = config;
                    }
                }
                if (monitorConfig != null) {
                    this.monitor = monitorConfig;
                    DubboBootstrap.getInstance().monitor(monitorConfig);
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        SpringExtensionFactory.addApplicationContext(applicationContext);
    }


}
