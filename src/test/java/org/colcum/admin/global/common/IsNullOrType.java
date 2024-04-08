package org.colcum.admin.global.common;

import org.mockito.ArgumentMatcher;

public class IsNullOrType<T> implements ArgumentMatcher<T> {

    private final Class<T> targetClass;

    public IsNullOrType(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public boolean matches(T argument) {
        return argument == null || targetClass.isInstance(argument);
    }

}
