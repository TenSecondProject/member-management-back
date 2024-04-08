package org.colcum.admin.global.util;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static void setField(Object targetObject, String fieldName, Object value) {
        try {
            Field field = targetObject.getClass().getDeclaredField(fieldName); // 필드 가져오기
            field.setAccessible(true); // 접근 가능하도록 설정
            field.set(targetObject, value); // 값 설정
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
