package cn.w2n0.genghiskhan.dubbo.filter;


import com.alibaba.fastjson.JSON;
import cn.w2n0.genghiskhan.dubbo.extend.TenantEntity;
import cn.w2n0.genghiskhan.dubbo.extend.TenantEvent;
import cn.w2n0.genghiskhan.utils.reflect.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.beanutil.JavaBeanAccessor;
import org.apache.dubbo.common.beanutil.JavaBeanDescriptor;
import org.apache.dubbo.common.beanutil.JavaBeanSerializeUtil;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.io.UnsafeByteArrayInputStream;
import org.apache.dubbo.common.io.UnsafeByteArrayOutputStream;
import org.apache.dubbo.common.serialize.Serialization;
import org.apache.dubbo.common.utils.PojoUtils;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;
import org.apache.dubbo.rpc.support.ProtocolUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.dubbo.common.constants.CommonConstants.*;
import static org.apache.dubbo.rpc.Constants.GENERIC_KEY;

/**
 * 核心filter
 *
 * @author 无量
 */
@SuppressWarnings("ALL")
@Activate(group = CommonConstants.PROVIDER, order = -10001)
@Slf4j
public class RewriteGenericFilter implements Filter, Filter.Listener {
    Object objClass = null;
    private final String GENERICFLAG = "true";
    private final String SYSTENANTID="sysTenantId";
    public void setParams(Map<String, Object> paramMap) {
        if (paramMap.containsKey(SYSTENANTID)) {
            TenantEntity tenantEntity = new TenantEntity();
            tenantEntity.setTenantId(paramMap.get(SYSTENANTID).toString());
            SpringContextUtil.getBean(TenantEvent.class).publishEvent(tenantEntity);

        }
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        boolean correct=(invocation.getMethodName().equals($INVOKE) || invocation.getMethodName().equals($INVOKE_ASYNC))
                && invocation.getArguments() != null
                && invocation.getArguments().length == 3
                && !GenericService.class.isAssignableFrom(invoker.getInterface());
        if (correct) {
            String name = ((String) invocation.getArguments()[0]).trim();
            String[] types = (String[]) invocation.getArguments()[1];
            Object[] args = (Object[]) invocation.getArguments()[2];
            try {
                Method method = ReflectUtils.findMethodByMethodSignature(invoker.getInterface(), name, types);
                Class<?>[] params = method.getParameterTypes();
                if (args == null) {
                    args = new Object[params.length];
                }
                String generic = invocation.getAttachment(GENERIC_KEY);

                if (types != null && args.length != types.length) {
                    throw new RpcException("args.length != types.length");
                }

                if (StringUtils.isBlank(generic)) {
                    generic = RpcContext.getContext().getAttachment(GENERIC_KEY);
                }

                if (StringUtils.isEmpty(generic)
                        || ProtocolUtils.isDefaultGenericSerialization(generic)
                        || ProtocolUtils.isGenericReturnRawResult(generic)) {
                    if (types == null && generic != null && GENERICFLAG.equals(generic)) {
                        Map<String, Object> paramMap = new HashMap<String, Object>(10);
                        paramMap = (Map<String, Object>) args[0];
                        setParams(paramMap);
                        Object[] objects = new Object[params.length];
                        objClass = SpringContextUtil.getBean(invoker.getInterface());
                        List<String> parmNameList = ReflectUtil.getParamterName(objClass.getClass(), name);
                        for (int i = 0; i < params.length; i++) {
                            String parmName = parmNameList.get(i);
                            Object object = paramMap.get(parmName);
                            if (object == null) {
                                objects[i] = null;
                            } else {
                                boolean bool = true;
                                try {
                                    bool = RewriteGenericFilter.isBaseDataType(params[i]);
                                } catch (Exception e) {
                                    log.error("RewriteGenericFilter.invoke", e);
                                }

                                if (bool) {
                                    objects[i] = object;
                                } else {
                                    objects[i] = JSON.parseObject(object.toString(), params[i]);
                                }

                            }
                        }
                        args = objects;
                    }
                    args = PojoUtils.realize(args, params, method.getGenericParameterTypes());
                } else if (ProtocolUtils.isJavaGenericSerialization(generic)) {
                    for (int i = 0; i < args.length; i++) {
                        if (byte[].class == args[i].getClass()) {
                            try (UnsafeByteArrayInputStream is = new UnsafeByteArrayInputStream((byte[]) args[i])) {
                                args[i] = ExtensionLoader.getExtensionLoader(Serialization.class)
                                        .getExtension(GENERIC_SERIALIZATION_NATIVE_JAVA)
                                        .deserialize(null, is).readObject(Object.class);
                            } catch (Exception e) {
                                throw new RpcException("Deserialize argument [" + (i + 1) + "] failed.", e);
                            }
                        } else {
                            throw new RpcException(
                                    "Generic serialization [" +
                                            GENERIC_SERIALIZATION_NATIVE_JAVA +
                                            "] only support message type " +
                                            byte[].class +
                                            " and your message type is " +
                                            args[i].getClass());
                        }
                    }
                } else if (ProtocolUtils.isBeanGenericSerialization(generic)) {
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] instanceof JavaBeanDescriptor) {
                            args[i] = JavaBeanSerializeUtil.deserialize((JavaBeanDescriptor) args[i]);
                        } else {
                            throw new RpcException(
                                    "Generic serialization [" +
                                            GENERIC_SERIALIZATION_BEAN +
                                            "] only support message type " +
                                            JavaBeanDescriptor.class.getName() +
                                            " and your message type is " +
                                            args[i].getClass().getName());
                        }
                    }
                } else if (ProtocolUtils.isProtobufGenericSerialization(generic)) {
                    // as proto3 only accept one protobuf parameter
                    if (args.length == 1 && args[0] instanceof String) {
                        try (UnsafeByteArrayInputStream is =
                                     new UnsafeByteArrayInputStream(((String) args[0]).getBytes())) {
                            args[0] = ExtensionLoader.getExtensionLoader(Serialization.class)
                                    .getExtension(GENERIC_SERIALIZATION_PROTOBUF)
                                    .deserialize(null, is).readObject(method.getParameterTypes()[0]);
                        } catch (Exception e) {
                            throw new RpcException("Deserialize argument failed.", e);
                        }
                    } else {
                        throw new RpcException(
                                "Generic serialization [" +
                                        GENERIC_SERIALIZATION_PROTOBUF +
                                        "] only support one " + String.class.getName() +
                                        " argument and your message size is " +
                                        args.length + " and type is" +
                                        args[0].getClass().getName());
                    }
                }

                RpcInvocation rpcInvocation = new RpcInvocation(method, invoker.getInterface().getName(), args, invocation.getObjectAttachments(), invocation.getAttributes());
                rpcInvocation.setInvoker(invocation.getInvoker());
                rpcInvocation.setTargetServiceUniqueName(invocation.getTargetServiceUniqueName());

                return invoker.invoke(rpcInvocation);
            } catch (NoSuchMethodException | ClassNotFoundException e) {
                throw new RpcException(e.getMessage(), e);
            }
        }
        return invoker.invoke(invocation);
    }

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        boolean correct=(invocation.getMethodName().equals($INVOKE) || invocation.getMethodName().equals($INVOKE_ASYNC))
                && invocation.getArguments() != null
                && invocation.getArguments().length == 3
                && !GenericService.class.isAssignableFrom(invoker.getInterface());
        if (correct) {
            String generic = invocation.getAttachment(GENERIC_KEY);
            if (StringUtils.isBlank(generic)) {
                generic = RpcContext.getContext().getAttachment(GENERIC_KEY);
            }
            if (appResponse.hasException()) {
                Throwable appException = appResponse.getException();
                if (appException instanceof GenericException) {
                    if (objClass != null) {
                        String name = ((String) invocation.getArguments()[0]).trim();
                        log.error("method:{},Exception:{}", name, appResponse);
                    }
                    GenericException tmp = (GenericException) appException;
                    appException = new GenericException(tmp.getExceptionClass(), tmp.getExceptionMessage());
                }
                if (!(appException instanceof GenericException)) {
                    appException = new GenericException(appException);
                }
                appResponse.setException(appException);
            }
            if (ProtocolUtils.isJavaGenericSerialization(generic)) {
                try {
                    UnsafeByteArrayOutputStream os = new UnsafeByteArrayOutputStream(512);
                    ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(GENERIC_SERIALIZATION_NATIVE_JAVA).serialize(null, os).writeObject(appResponse.getValue());
                    appResponse.setValue(os.toByteArray());
                } catch (IOException e) {
                    throw new RpcException(
                            "Generic serialization [" +
                                    GENERIC_SERIALIZATION_NATIVE_JAVA +
                                    "] serialize result failed.", e);
                }
            } else if (ProtocolUtils.isBeanGenericSerialization(generic)) {
                appResponse.setValue(JavaBeanSerializeUtil.serialize(appResponse.getValue(), JavaBeanAccessor.METHOD));
            } else if (ProtocolUtils.isProtobufGenericSerialization(generic)) {
                try {
                    UnsafeByteArrayOutputStream os = new UnsafeByteArrayOutputStream(512);
                    ExtensionLoader.getExtensionLoader(Serialization.class)
                            .getExtension(GENERIC_SERIALIZATION_PROTOBUF)
                            .serialize(null, os).writeObject(appResponse.getValue());
                    appResponse.setValue(os.toString());
                } catch (IOException e) {
                    throw new RpcException("Generic serialization [" +
                            GENERIC_SERIALIZATION_PROTOBUF +
                            "] serialize result failed.", e);
                }
            } else if (ProtocolUtils.isGenericReturnRawResult(generic)) {
                return;
            } else {
                appResponse.setValue(PojoUtils.generalize(appResponse.getValue()));
            }
        }
    }

    @Override
    public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {

    }

    /**
     * 判断一个类是否为基本数据类型。
     *
     * @param clazz 要判断的类。
     * @return true 表示为基本数据类型。
     */
    public static boolean isBaseDataType(Class clazz) throws Exception {
        return
                (
                        clazz.equals(String.class) ||
                                clazz.equals(Integer.class) ||
                                clazz.equals(Byte.class) ||
                                clazz.equals(Long.class) ||
                                clazz.equals(Double.class) ||
                                clazz.equals(Float.class) ||
                                clazz.equals(Character.class) ||
                                clazz.equals(Short.class) ||
                                clazz.equals(BigDecimal.class) ||
                                clazz.equals(BigInteger.class) ||
                                clazz.equals(Boolean.class) ||
                                clazz.equals(Date.class) ||
                                clazz.isPrimitive()
                );

    }
}