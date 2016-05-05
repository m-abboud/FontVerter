package org.fontverter.validator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Validates input <T> object against validation rules. Rules use the @ValidateRule annotation above methods and can be
 * any object type.
 */
public abstract class RuleValidator<T> {
    private T toValidate;
    private List<Object> ruleDefinitions = new ArrayList<Object>();
    List<FontValidatorError> errors = new LinkedList<FontValidatorError>();

    public List<FontValidatorError> validate(T toValidate) throws InvocationTargetException, IllegalAccessException {
        errors.clear();
        this.toValidate = toValidate;

        for (Object ruleOn : ruleDefinitions)
            evaluateRuleDefinition(ruleOn);

        return errors;
    }

    public void validateWithExceptionsThrown(T toValidate) throws InvocationTargetException, IllegalAccessException, FontValidationException {
        this.toValidate = toValidate;
        List<FontValidatorError> errors = validate(this.toValidate);

        String validateMessage = "";
        for (FontValidatorError errorOn : errors)
            validateMessage += "\n" + errorOn.toString();

        if (errors.size() > 0)
            throw new FontValidationException("Internal Validator error(s) " + validateMessage);
    }

    public void addRuleDefinition(Object ruleDefinition) {
        this.ruleDefinitions.add(ruleDefinition);
    }

    private void evaluateRuleDefinition(Object ruleOn) throws IllegalAccessException, InvocationTargetException {
        for (Method methodOn : ruleOn.getClass().getDeclaredMethods()) {
            if (methodOn.isAnnotationPresent(ValidateRule.class))
                evaluateRule(methodOn, ruleOn);
        }
    }

    private void evaluateRule(Method methodOn, Object ruleDef) throws IllegalAccessException, InvocationTargetException {
        ValidateRule annotation = methodOn.getAnnotation(ValidateRule.class);

        Object methodResult = methodOn.invoke(ruleDef, toValidate);

        boolean isValid = true;
        String field = "";
        if (methodResult instanceof Boolean)
            isValid = (Boolean) methodResult;
        if (methodResult instanceof String) {
            field = (String) methodResult;
            isValid = field.isEmpty();
        }

        if (!isValid) {
            String className = ruleDef.getClass().getSimpleName();
            String message = String.format("%s.%s %s \nFIELD VALUE: %s", className, methodOn.getName(), annotation.message(), field);

            errors.add(new FontValidatorError(annotation.type(), message));
        }
    }

    public enum ValidatorErrorType {
        ERROR, WARNING, INFO, NONE
    }

    public static class FontValidatorError {
        private final ValidatorErrorType type;
        private final String message;

        public FontValidatorError(ValidatorErrorType type, String message) {
            this.type = type;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public ValidatorErrorType getType() {
            return type;
        }

        @Override
        public String toString() {
            return "Validate " + getType() + ": " + getMessage();
        }
    }

}
