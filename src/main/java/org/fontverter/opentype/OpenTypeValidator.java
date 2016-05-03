package org.fontverter.opentype;

import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class OpenTypeValidator {
    private OpenTypeFont font;

    public List<FontValidatorError> validate(OpenTypeFont font) throws InvocationTargetException, IllegalAccessException {
        this.font = font;
        List<FontValidatorError> errors = new LinkedList<FontValidatorError>();

        for (Method methodOn : this.getClass().getDeclaredMethods()) {
            if (methodOn.isAnnotationPresent(ValidateRule.class))
                evaluateRule(errors, methodOn);
        }

        return errors;
    }

    public void validateWithExceptionsThrown(OpenTypeFont font) throws InvocationTargetException, IllegalAccessException, FontValidationException {
        this.font = font;
        List<FontValidatorError> errors = validate(this.font);

        String validateMessage = "";
        for (FontValidatorError errorOn : errors)
            validateMessage += "\n" + errorOn.toString();

        if (errors.size() > 0)
            throw new FontValidationException("Internal Validator error(s) " + validateMessage);
    }

    private void evaluateRule(List<FontValidatorError> errors, Method methodOn) throws IllegalAccessException, InvocationTargetException {
        ValidateRule annotation = methodOn.getAnnotation(ValidateRule.class);

        Boolean result = (Boolean) methodOn.invoke(this);
        if (!result)
            errors.add(new FontValidatorError(annotation.type(), annotation.message() + " " + methodOn.getName()));
    }

    @OpenTypeValidator.ValidateRule
    public boolean hheaDescender() {
        return font.hhea.descender < 0;
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

    public static enum ValidatorErrorType {
        ERROR, WARNING, INFO
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface ValidateRule {
        ValidatorErrorType type() default ValidatorErrorType.ERROR;

        String message() default "";
    }

    public class FontValidationException extends Exception {
        public FontValidationException(String message) {
            super(message);
        }
    }
}
