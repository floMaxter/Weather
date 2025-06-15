package com.projects.weather.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersistenceExceptionUtil {

    public static boolean isUniqueConstraintViolation(Throwable throwable, String constraintName) {
        var currentThrowable = throwable;
        while (currentThrowable != null) {
            if (currentThrowable instanceof ConstraintViolationException violationException) {
                if (constraintName.equals(violationException.getConstraintName())) {
                    return true;
                }
            }
            currentThrowable = currentThrowable.getCause();
        }

        return false;
    }
}
