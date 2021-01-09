package de.eldoria.eldoutilities.serialization;

import de.eldoria.eldoutilities.utils.EnumUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * Map for type resolving.
 *
 * @since 1.0.0
 */
public final class TypeResolvingMap extends AbstractMap<String, Object> {
    private final Map<String, Object> delegate;

    TypeResolvingMap(Map<String, Object> delegate) {
        this.delegate = delegate;
    }

    @NotNull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return delegate.entrySet();
    }

    /**
     * Get a value from map.
     *
     * @param key key
     * @return object or null if key is not present
     */
    @Override
    public Object get(Object key) {
        return this.delegate.get(key);
    }

    /**
     * Get a value from map.
     *
     * @param key key
     * @param <T> type of return value
     * @return object or null if key is not present
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        return (T) get(key);
    }

    public <T, V> void listToMap(Map<T, V> map, String key, Function<V, T> valueToKey) {
        List<V> values = getValue(key);
        values.forEach(v -> map.put(valueToKey.apply(v), v));
    }

    /**
     * Get a value from map.
     *
     * @param key          key
     * @param defaultValue default value if key does not exist
     * @param <T>          type of return value
     * @return value of key or default value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueOrDefault(String key, T defaultValue) {
        return (T) delegate.getOrDefault(key, defaultValue);
    }

    public UUID getValueOrDefault(String key, UUID defaultValue) {
        return getValueOrDefault(key, defaultValue, UUID::fromString);
    }

    /**
     * Get a value from map.
     *
     * @param key          key
     * @param defaultValue default value if key does not exist
     * @param clazz        enum clazz to resolve
     * @param <T>          type of return value
     * @return value of key or default value
     */
    public <T extends Enum<T>> T getValueOrDefault(String key, T defaultValue, Class<T> clazz) {
        return EnumUtil.parse(getValueOrDefault(key, defaultValue.name()), clazz, defaultValue);
    }

    /**
     * Get a value from map.
     *
     * @param key            key
     * @param defaultValue   default value if key does not exist
     * @param valueConverter Function to parse the string to value
     * @param <T>            type of return value
     * @return value of key or default value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueOrDefault(String key, T defaultValue, Function<String, T> valueConverter) {
        if (containsKey(key)) {
            return getValue(key, valueConverter);
        }
        return defaultValue;
    }

    /**
     * Get a value from map.
     *
     * @param key            key
     * @param valueConverter Function to parse the string to value.
     * @param <T>            type of return value
     * @return converted string or null if key is not present.
     */
    public <T> T getValue(String key, Function<String, T> valueConverter) {
        String value = getValue(key);
        return value == null ? null : valueConverter.apply(value);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }
}