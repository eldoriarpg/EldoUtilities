package de.eldoria.eldoutilities.serialization;

import com.google.common.collect.ObjectArrays;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class SerializationUtil {
    private static final NamingStrategy NAMING_STRATEGY = new KebabNamingStrategy();

    private SerializationUtil() {

    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Map<String, Object> map) {
        return new Builder(map);
    }

    public static <T, U> BiFunction<T, U, String> keyToString() {
        return (k, v) -> k.toString();
    }

    public static <T, U> BiFunction<T, U, Object> valueOnly(Function<U, ?> valueFunction) {
        return (k, v) -> valueFunction.apply(v);
    }

    public static <T, U> BiFunction<T, U, String> keyToPrefixedString(String prefix) {
        return (k, v) -> prefix + k.toString();
    }

    public static TypeResolvingMap mapOf(Map<String, Object> serialized) {
        return new TypeResolvingMap(serialized);
    }

    public static final class Builder {
        private final Map<String, Object> serialized;

        public Builder() {
            serialized = new LinkedHashMap<>();
        }

        public Builder(Map<String, Object> map) {
            serialized = new LinkedHashMap<>(map);
        }

        /**
         * Adda a key with a object.
         *
         * @param key   key
         * @param value value to add
         *
         * @return builder with values changed
         */
        public Builder add(String key, Object value) {
            this.serialized.put(key, value);
            return this;
        }

        /**
         * @param key      key
         * @param value    value to add
         * @param toString method to convert value to string
         * @param <T>      type of value
         *
         * @return builder with values changed
         */
        public <T> Builder add(String key, T value, Function<T, String> toString) {
            return add(key, toString.apply(value));
        }

        /**
         * Add a key with a enum constant name
         *
         * @param key       key
         * @param enumValue enum value
         *
         * @return builder with values changed
         */
        public Builder add(String key, Enum<?> enumValue) {
            return add(key, enumValue.name());
        }

        /**
         * Adds a key with a collection which will be wrapped in a list.
         *
         * @param key        key
         * @param collection collection
         *
         * @return builder with values changed
         */
        public Builder add(String key, Collection<?> collection) {
            this.serialized.put(key, new ArrayList<>(collection)); // serialize collection as list
            return this;
        }

        /**
         * Adds a object. The key will be computed by the current naming strategy.
         *
         * @param value value to add
         *
         * @return builder with values changed
         */
        public Builder add(Object value) {
            return add(NAMING_STRATEGY.adapt(value.getClass()), value);
        }

        /**
         * Adds a enum value. The key will be computed by the current naming strategy.
         *
         * @param enumValue value to add
         *
         * @return builder with values changed
         */
        public Builder add(Enum<?> enumValue) {
            return add(NAMING_STRATEGY.adapt(enumValue.getClass()), enumValue);
        }

        /**
         * Add a map to the map
         *
         * @param map           map to add
         * @param keyFunction   function to map key to string
         * @param valueFunction function to map value to object
         * @param <K>           key type
         * @param <V>           value type
         *
         * @return builder with values changed
         */
        public <K, V> Builder add(Map<K, V> map, BiFunction<K, V, String> keyFunction,
                                  BiFunction<K, V, Object> valueFunction) {
            map.forEach((k, v) -> add(keyFunction.apply(k, v), valueFunction.apply(k, v)));
            return this;
        }

        /**
         * Add a map to the serialization map
         *
         * @param map map to add
         *
         * @return builder with values changed
         */
        public Builder add(Map<String, Object> map) {
            map.forEach(this::add);
            return this;
        }

        public Map<String, Object> build() {
            return this.serialized;
        }
    }

    public static Map<String, Object> objectToMap(Object obj) {
        Builder builder = newBuilder();
        Field[] declaredFields = getAllFields(obj);
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            if (!Modifier.isTransient(declaredField.getModifiers())) {
                try {
                    builder.add(declaredField.getName(), declaredField.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.build();
    }

    public static <T> void mapOnObject(Map<String, Object> objectMap, T obj) {
        Field[] declaredFields = getAllFields(obj);
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            if (!Modifier.isTransient(declaredField.getModifiers())) {
                if (!objectMap.containsKey(declaredField.getName())) continue;
                try {
                    declaredField.set(obj, objectMap.get(declaredField.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Field[] getAllFields(Object obj) {
        Field[] fields = new Field[0];
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
            fields = ObjectArrays.concat(fields, clazz.getDeclaredFields(), Field.class);
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}