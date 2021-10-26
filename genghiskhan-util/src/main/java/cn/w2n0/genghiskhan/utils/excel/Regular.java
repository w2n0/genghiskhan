package cn.w2n0.genghiskhan.utils.excel;

/**
 * excel 列校验表达式
 * @author 无量
 */

public enum Regular {
    /**
     * 制表符
     */
    tabs("[\\s\\S]*\t[\\s\\S]*"),
    /**
     * 回车符
     */
    carriageReturn("[\\s\\S]*\r[\\s\\S]*"),
    /**
     * 换行符
     */
    lineBreak("[\\s\\S]*\n[\\s\\S]*"),
    /**
     * 换页符
     */
    formFeedCharacter ("[\\s\\S]*\f[\\s\\S]*"),
    /**
     * 手机号
     */
    phone("^1[3456789]\\d{9}$"),
    /**
     * email
     */
    Email("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"),
    /**
     * 域名
     */
    domain("[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(/.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+/.?"),
    /**
     * 内部链接
     */
    internetURL("[a-zA-z]+://[^\\s]* 或 ^http://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$"),
    /**
     * 身份证号
     */
    idNumber("^\\d{15}|\\d{18}$");
    private final String value;
    private Regular (String value) {
        this.value = value;
    }
    public String value ()
    {
        return value;
    }
}