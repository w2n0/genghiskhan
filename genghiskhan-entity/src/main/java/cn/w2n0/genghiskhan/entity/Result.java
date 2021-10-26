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
     * @param success
     * @param code
     * @param msg
     * @param result
     */
    public Result(boolean success, int code, String msg, T result) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public Result(int code, String msg) {
        this.success = false;
        this.code = code;
        this.msg = msg;
        this.result = null;
    }

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
     * @param t
     * @param <T>
     * @return
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
     * @param <T>
     * @return
     */
    public static <T> Result<T> sucess() {
        Result result = new Result();
        result.setCode(SUCCESSCODE);
        result.setSuccess(true);
        return result;
    }

    /**
     * 失败
     * @param code
     * @param <T>
     * @return
     */
    public static <T> Result<T> failed(int code) {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(code);
        return result;
    }
    /**
     * 失败
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> Result<T> failed(String msg) {
        Result result = new Result();
        result.setSuccess(false);
        result.setMsg(msg);
        return result;
    }
}
