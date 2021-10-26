package cn.w2n0.genghiskhan.dubbo.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.MDC;

/**
 * dubbo添加调用链Id
 * @author 无量
 * @date: 2021/3/22 10:58
 */
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER})
public class DubboTraceIdFilter implements Filter {
    private final String TRACEID = "traceId";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext rpcContext = RpcContext.getContext();
        if (rpcContext.getUrl()==null) {
            String traceId = invocation.getAttachment(TRACEID);
            MDC.put(TRACEID, traceId);
        }
        else{
            String traceId = MDC.get(TRACEID);
            rpcContext.setAttachment(TRACEID, traceId);
        }
        Result result = invoker.invoke(invocation);
        if (rpcContext.isProviderSide()) {
            MDC.remove(TRACEID);
        }
        return result;
    }
}
