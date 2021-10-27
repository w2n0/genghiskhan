package cn.w2n0.genghiskhan.dubbo.router;

/**
 * @author 无量
 * date: 2021/5/6 11:32
 */
public enum RouterType {
    /**
     * 条件
     */
    condition("condition"),
    /**
     * hash
     */
    hash("hash");
    String value;

    RouterType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
