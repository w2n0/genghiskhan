package cn.w2n0.genghiskhan.sequence.snowflake;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.w2n0.genghiskhan.sequence.SnGenerate;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import javax.annotation.PostConstruct;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * 雪花算法
 * todo:项目组的应用是容器多实例启动的，workId和dataCenterId需要分布式动态生成
 *
 * @author 无量
 */
public class SnowFlakeGenerate implements SnGenerate {


    private Snowflake snowflake;
    private volatile static SnowFlakeGenerate ins = null;

    /**
     * 单例
     *
     * @return SnowFlakeGenerate
     */
    public static SnowFlakeGenerate getInstance() {
        if (ins == null) {
            synchronized (SnowFlakeGenerate.class) {
                if (ins == null) {
                    ins = new SnowFlakeGenerate();
                }
            }
        }
        return ins;
    }

    /**
     * 构造
     */
    private SnowFlakeGenerate() {
        long workerId = 0;
        try {
            //todo 相同机器不同容器的IP获取方法
            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
        } catch (Exception e) {
            workerId = NetUtil.getLocalhostStr().hashCode();
        }
       snowflake = IdUtil.createSnowflake(getWorkId(), getDataCenterId());

    }
    /**
     * workId使用IP生成
     * @return workId
     */
    private  Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums = sums + b;
            }
            return (long) (sums % 32);
        }
        catch (UnknownHostException e) {
            // 失败就随机
            return RandomUtils.nextLong(0, 31);
        }
    }

    /**
     * dataCenterId使用hostName生成
     * @return dataCenterId
     */
    private  Long getDataCenterId() {
        try {
            String hostName = SystemUtils.getHostName();
            int[] ints = StringUtils.toCodePoints(hostName);
            int sums = 0;
            for (int i: ints) {
                sums = sums + i;
            }
            return (long) (sums % 32);
        }
        catch (Exception e) {
            // 失败就随机
            return RandomUtils.nextLong(0, 31);
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
