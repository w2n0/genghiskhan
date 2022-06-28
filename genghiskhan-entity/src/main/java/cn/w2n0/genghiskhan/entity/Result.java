package cn.w2n0.genghiskhan.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 返回结果
 *
 * @author 无量
 */
@Data
public class Result<T> implements Serializable {

    private static final int SUCCESSCODE = 10000;
    private boolean success;
    private String msg;
    private int code;
    private T result;


    public Result() {
        this.success = true;
    }

    /**
     * @param success 成功标记
     * @param code 错误码
     * @param msg 错误消息
     * @param result 返回内容
     */
    public Result(boolean success, int code, String msg, T result) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    /**
     * @param code 错误码
     * @param msg 错误消息
     */
    public Result(int code, String msg) {
        this.success = false;
        this.code = code;
        this.msg = msg;
        this.result = null;
    }

    /**
     * @param result 处理结果
     */
    public Result(T result) {
        this.success = true;
        this.code = 0;
        this.msg = "";
        this.result = result;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getResult() {
        return this.result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    /**
     * 成功
     * @param t 结果
     * @param <T> 泛型
     * @return Result
     */
    public static <T> Result<T> sucess(T t) {
        Result result = new Result();
        result.setCode(SUCCESSCODE);
        result.setSuccess(true);
        result.setResult(t);
        return result;
    }

    /**
     * 成功
     * @return Result
     */
    public static  Result sucess() {
        Result result = new Result();
        result.setCode(SUCCESSCODE);
        result.setSuccess(true);
        return result;
    }
    /**
     * 失败
     * @param code 错误码
     * @param msg 错误消息
     * @return Result
     */
    public static  Result failed(int code,String msg) {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    /**
     * 失败
     * @param code 错误码
     * @return Result
     */
    public static Result failed(int code) {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(code);
        return result;
    }
    /**
     * 失败
     * @param msg 错误消息
     * @return Result
     */
    public static Result failed(String msg) {
        Result result = new Result();
        result.setSuccess(false);
        result.setMsg(msg);
        return result;
    }

}
