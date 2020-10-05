package de.eldoria.eldoutilities.serialization;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

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
     *
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
     *
     * @return object or null if key is not present
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        return (T) get(key);
    }

    /**
     * Get a value from map.
     *
     * @param key          key
     * @param defaultValue default value if key does not exist
     * @param <T>          type of return value
     *
     * @return value of key or default value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueOrDefault(String key, T defaultValue) {
        return (T) delegate.getOrDefault(key, defaultValue);
    }

    /**
     * Get a value from map.
     *
     * @param key            key
     * @param defaultValue   default value if key does not exist
     * @param valueConverter Function to parse the string to value
     * @param <T>            type of return value
     *
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
     *
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