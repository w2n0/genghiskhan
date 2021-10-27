package cn.w2n0.genghiskhan.dubbo.router;

import cn.w2n0.genghiskhan.dubbo.filter.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.router.AbstractRouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 灰度路由
 * 前提:开启灰度路由(dubbo.consumer.router.enable==true)
 * 如果匹配规则返回灰度IP
 * 如果不匹配规则返回正常IP（非灰IP）
 *
 * @author 无量
 * date: 2021/4/11 14:37
 */
@Slf4j
public class GrayRouter extends AbstractRouter {
    private static final int GRAY_ROUTER_DEFAULT_PRIORITY = 100;
    private static final String GENERICSERVICE = "org.apache.dubbo.rpc.service.GenericService";

    public GrayRouter(URL url) {
        super(url);
        this.priority = GRAY_ROUTER_DEFAULT_PRIORITY;
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        RouterProperties routerProperties = SpringContextUtil.getBean(RouterProperties.class);
        if (routerProperties != null&&routerProperties.isEnable()) {
            boolean isGray = false;
            String value = "";
            if (GENERICSERVICE.equals(invocation.getServiceName()) &&
                    invocation.getArguments() != null &&
                    invocation.getArguments().length == 3 &&
                    invocation.getArguments()[2] instanceof Object[]
                    ) {
                Object[] objects = (Object[]) invocation.getArguments()[2];
                HashMap paramMap = (HashMap) objects[0];
                if (paramMap.containsKey(routerProperties.getParam())) {
                    value = (String) paramMap.get(routerProperties.getParam());

                }
            } else if (invocation.getObjectAttachments().size() > 0) {
                value = invocation.getAttachment(routerProperties.getParam());
            }
            if (StringUtils.isNotEmpty(value)) {
                if (RouterType.condition.toString().equals(routerProperties.getRule())) {
                    if (routerProperties.getValues().indexOf(value) > -1) {
                        isGray = true;
                        invokers = route(routerProperties.getRouters(), invokers);
                    }
                } else if (RouterType.hash.toString().equals(routerProperties.getRule())) {
                    if (value.hashCode() % GRAY_ROUTER_DEFAULT_PRIORITY <= Integer.valueOf(routerProperties.getValues())) {
                        isGray = true;
                        invokers = route(routerProperties.getRouters(), invokers);
                    }
                }

            }
            if (isGray) {
                invokers = route(routerProperties.getRouters(), invokers);
            } else {
                invokers = excludeGrayRoute(routerProperties.getRouters(), invokers);
            }
        }
        return invokers;
    }

    /**
     * 路由算法
     * 如果所有IP中包含灰度IP则返回灰度IP
     *
     * @param routers  灰度IP
     * @param invokers 所有IP
     * @return
     */
    private <T> List<Invoker<T>> route(List<String> routers, List<Invoker<T>> invokers) {
        List<Invoker<T>> invokerList = new ArrayList<>();
        if (routers != null) {
            for (Invoker<T> invoker : invokers) {
                if (routers.contains(invoker.getUrl().getIp())) {
                    invokerList.add(invoker);
                }
            }
        }
        return invokerList;
    }


    /**
     * 路由算法
     * 返回灰度IP之外的其它IP
     *
     * @param routers  灰度IP
     * @param invokers 所有IP
     * @return
     */
    private <T> List<Invoker<T>> excludeGrayRoute(List<String> routers, List<Invoker<T>> invokers) {
        List<Invoker<T>> invokerList = new ArrayList<>();
        for (Invoker<T> invoker : invokers) {
            if (routers == null || !routers.contains(invoker.getUrl().getIp())) {
                invokerList.add(invoker);
            }
        }
        return invokerList;
    }

}
