package cn.w2n0.genghiskhan.sequence.snowflake;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.w2n0.genghiskhan.sequence.SnGenerate;

import javax.annotation.PostConstruct;

/**
 * 雪花算法
 * @author 无量
 */
public class SnowFlakeGenerate implements SnGenerate {

    private long workerId = 0L;
    private long datacenterId = 1L;
    private Snowflake snowflake = IdUtil.createSnowflake(workerId, datacenterId);
    private volatile static SnowFlakeGenerate ins = null;

    /**
     * 单例
     * @return SnowFlakeGenerate
     */
    public static SnowFlakeGenerate getInstance(){
        if (ins == null){
            synchronized (SnowFlakeGenerate.class){
                if (ins == null){
                    ins = new SnowFlakeGenerate();
                }
            }
        }
        return ins;
    }

    /**
     * 构造
     */
    public SnowFlakeGenerate(){
         init();
    }
    @PostConstruct
    public void init() {
        try {
            //todo 相同机器不同容器的IP获取方法
            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
        } catch (Exception e) {
            workerId = NetUtil.getLocalhostStr().hashCode();
        }
    }

    public synchronized long snowflakeId() {
        return snowflake.nextId();
    }

    @Override
    public synchronized String gen() {
        return String.valueOf(snowflake.nextId());
    }

}
