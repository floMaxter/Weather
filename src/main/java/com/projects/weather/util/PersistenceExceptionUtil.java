package com.projects.weather.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceExceptionUtil {

    public static boolean isUniqueConstraintViolation(Throwable throwable, String constraintKey) {
        var currentThrowable = throwable;
        while (currentThrowable != null) {
            if (currentThrowable instanceof ConstraintViolationException violationException) {
                var constraintName = violationException.getConstraintName();
                if (constraintName != null && constraintName.toLowerCase().contains(constraintKey)) {
                    return true;
                }
            }
            currentThrowable = currentThrowable.getCause();
        }

        return false;
    }
}
