package cn.w2n0.genghiskhan.dubbo.router;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;

/**
 * @author 无量
 * @date: 2021/4/11 14:37
 */
public class GrayRouterFactory implements RouterFactory {
    @Override
    public Router getRouter(URL url) {
        return new GrayRouter(url);
    }
}
