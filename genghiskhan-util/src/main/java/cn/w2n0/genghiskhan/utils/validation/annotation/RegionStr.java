package cn.w2n0.genghiskhan.utils.validation.annotation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;


/**
 * 项目清单：sms
 * @author 无量
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {RegionStr.RegionStrValidator.class})
public @interface RegionStr {
    String message() default "The string is not optional";

    String[] values();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    public static class RegionStrValidator implements ConstraintValidator<RegionStr, String> {

        String[] values;

        @Override
        public void initialize(RegionStr constraintAnnotation) {
            values = constraintAnnotation.values();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (Arrays.asList(this.values).contains(value)) {
                return true;
            }
            return false;
        }

    }
}