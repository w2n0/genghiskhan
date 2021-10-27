package cn.w2n0.genghiskhan.dubbo.extend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 租户事件
 *
 * @author 无量
 */
public class TenantEvent {

    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * 下发
     * @param tenantEntity 租户实体
     */
    public void publishEvent(TenantEntity tenantEntity) {
        // 发布下单事件
        publisher.publishEvent(tenantEntity);
    }
}
