package de.eldoria.eldoutilities.utils;

import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

public final class ReflectionUtil {
    private static final String ANNOTATION_METHOD = "annotationData";
    private static final String ANNOTATIONS = "annotations";

    private ReflectionUtil() {
    }


    public static void changeSerializedName(Class<?> targetClass, Function<String, String> map)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        SerializableAs serializable = targetClass.getAnnotation(SerializableAs.class);
        SerializableAs serializableAs = new SerializableAs() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return serializable.annotationType();
            }

            @Override
            public @NotNull String value() {
                return map.apply(serializable.value());
            }
        };
        alterAnnotationValue(targetClass, SerializableAs.class, serializableAs);
    }

    @SuppressWarnings("unchecked")
    public static void alterAnnotationValue(Class<?> targetClass, Class<? extends Annotation> targetAnnotation, Annotation targetValue)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method method = Class.class.getDeclaredMethod(ANNOTATION_METHOD, null);
        method.setAccessible(true);

        Object annotationData = method.invoke(targetClass);

        Field annotations = annotationData.getClass().getDeclaredField(ANNOTATIONS);
        annotations.setAccessible(true);

        Map<Class<? extends Annotation>, Annotation> map = (Map<Class<? extends Annotation>, Annotation>) annotations.get(annotationData);
        map.put(targetAnnotation, targetValue);
    }
}
