package cn.w2n0.genghiskhan.utils.validation;


import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.util.Set;

/**
 * 校验工具类
 *
 * @author 无量
 */
public class ValidationUtils {
    public static void validate(@Valid Object entity) {
        Set<ConstraintViolation<@Valid Object>> validateSet = Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(entity, new Class[0]);
        if (!CollectionUtils.isEmpty(validateSet)) {
            String messages = validateSet.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "；" + m2)
                    .orElse("参数输入有误！");
            throw new IllegalArgumentException(messages);
        }
    }
}
