package cn.w2n0.genghiskhan.utils.excel;
import java.lang.annotation.*;

/**
 * excle 列
 * @author 无量
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
    boolean required() default false;
    String code();
    boolean primarykey() default false;
    Regular[] regular() default {};
    Regular[] noRegular() default {};
}