package com.marakicode.securepay.users;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExactlyOneOfValidator implements ConstraintValidator<ExactlyOneOf, Object> {
    private String[] fields;

    @Override
    public void initialize(ExactlyOneOf constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return true;
        int nonNullCount = 0;

        for (var field : fields) {
            try {
                var fieldValue = value.getClass()
                        .getDeclaredMethod(field)
                        .invoke(value);
                if (fieldValue != null && !fieldValue.toString().isBlank())
                    nonNullCount++;

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return nonNullCount == 1;
    }
}
