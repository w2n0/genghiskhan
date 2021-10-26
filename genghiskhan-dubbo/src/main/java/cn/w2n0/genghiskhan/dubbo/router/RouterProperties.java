package cn.w2n0.genghiskhan.dubbo.router;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 路由配置
 * @author 无量
 */
@Data
@ConfigurationProperties(prefix = "dubbo.consumer.router")
public class RouterProperties {
    /**
     * 是否启用
     */
    private boolean enable;
    /**
     * 规则，rule=condition | hash
     * condition  根据参数及value值确定调用列表
     * hash 根据参数值的hash值匹配value,value范围1-10
     */
    private String rule;

    /**
     * 参数
     */
    private String param;
    /**
     * 如果是condition 模式则values 为param参数对应的值，多个','号分割
     * 如果是hash 模式则values 为 1-100数字，意为百分比
     */
    private String values;
    /**
     * 灰度目标
     */
    private List<String> routers;
}
