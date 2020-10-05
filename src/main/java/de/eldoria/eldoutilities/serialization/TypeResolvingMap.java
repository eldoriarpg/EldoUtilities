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

    @Override
    public Object get(Object key) {
        return this.delegate.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        return (T) get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValueOrDefault(String key, T defaultValue) {
        return (T) getOrDefault(key, defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValueOrDefault(String key, T defaultValue, Function<String, T> valueConverter) {
        if (containsKey(key)) {
            return getValue(key, valueConverter);
        }
        return defaultValue;
    }

    public <T> T getValue(String key, Function<String, T> valueConverter) {
        return valueConverter.apply(getValue(key));
    }
}