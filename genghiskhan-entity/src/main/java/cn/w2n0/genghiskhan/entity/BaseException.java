package cn.w2n0.genghiskhan.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 基础异常
 * @author 无量
 */
@Data
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private int code;
    private String msg;

    public BaseException() {

    }

    public BaseException(int code) {
        this.code = code;
    }
}