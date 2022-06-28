package cn.w2n0.genghiskhan.sequence;

/**
 * 序列号生成规则
 * todo
 * 支持随机算法和规则算法
 * 随机算法：snowflake
 *         需配置workId生成器类型（redis、db）包括连接串、账号、密码等，并配置高可用
 *         可配置前缀
 * 规则算法：表达式+随机数，如CKyyyyMMdd****代表CK202203110001
 *         其中随机数用*代替一个*代表一个位置，随机数可强制顺序也可不固定顺序
 *                强制顺序需要用到redis或者数据库做自增处理
 *                不固定顺序则需要服务启动后按照表达式作为序号分组，在redis或数据库中做滑动分片，然后拿到分片号
 *
 * @author 无量
 */
public interface SnGenerate {

    /**
     * 生成字符型序列号
     *
     * @return 字符型序列号， 通常由字符和数字组合而成
     */
    String gen();


}
