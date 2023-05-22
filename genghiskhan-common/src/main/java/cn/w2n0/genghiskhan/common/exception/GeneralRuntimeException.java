package cn.w2n0.genghiskhan.common.exception;

/**
 * 异常处理接口
 * @author wuliang
 */
public interface GeneralRuntimeException  {

    /**
     * errCode
     * @return 错误码
     */
    public int getErrCode();

    /**
     * errMsg
     * @return 错误消息
     */
    public String getErrMsg();

}
