package org.mabb.fontverter.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ValidateRule {
    RuleValidator.ValidatorErrorType type() default RuleValidator.ValidatorErrorType.ERROR;

    String message() default "";
}
