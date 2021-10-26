package cn.w2n0.genghiskhan.utils.validation.annotation;


import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author 无量
 * @date 2021/10/8 19:21
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {EnumValid.EnumValidator.class})
public @interface EnumValid {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 目标枚举类
     */
    Class<?> target() default Class.class;

    /**
     * 是否忽略空值
     */
    boolean ignoreEmpty() default true;

    /**
     * 枚举验证
     * @author 无量
     * @date 2021/10/8 19:21
     */
    public class EnumValidator implements ConstraintValidator<EnumValid, String> {

        private EnumValid annotation;

        @Override
        public void initialize(EnumValid constraintAnnotation) {

            annotation = constraintAnnotation;
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
            boolean result = false;

            Class<?> cls = annotation.target();
            boolean ignoreEmpty = annotation.ignoreEmpty();
            boolean success=(cls.isEnum() && (StringUtils.isNotEmpty(value)) || !ignoreEmpty);
            if (success) {

                Object[] objects = cls.getEnumConstants();
                try {
                    Method method = cls.getMethod("name");
                    for (Object obj : objects) {
                        Object code = method.invoke(obj);
                        if (value.equals(code.toString())) {
                            result = true;
                            break;
                        }
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    result = false;
                }
            } else {
                result = true;
            }
            return result;
        }
    }
}