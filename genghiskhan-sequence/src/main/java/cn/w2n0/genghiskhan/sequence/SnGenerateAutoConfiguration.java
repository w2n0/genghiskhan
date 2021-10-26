package cn.w2n0.genghiskhan.sequence;

import cn.w2n0.genghiskhan.sequence.snowflake.SnowFlakeGenerate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * sn码生成配置
 * @author 无量
 */
@Configuration
public class SnGenerateAutoConfiguration {
    @Bean
    public SnGenerate getGenerate() {
        return new SnowFlakeGenerate();
    }
}
