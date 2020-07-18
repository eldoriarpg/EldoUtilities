package de.eldoria.eldoutilities.crossversion.function;

import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.crossversion.UnsupportedVersionException;

import java.util.Map;
import java.util.function.BiFunction;

public class BiVersionFunction<A, B, R> {
    private final Map<ServerVersion, BiFunction<A, B, R>> functions;

    public BiVersionFunction(Map<ServerVersion, BiFunction<A, B, R>> functions) {
        this.functions = functions;
    }

    public R apply(A a, B b) {
        BiFunction<A, B, R> function = functions.get(ServerVersion.CURRENT_VERSION);
        if (function == null) {
            throw new UnsupportedVersionException(ServerVersion.CURRENT_VERSION);
        }
        return function.apply(a, b);
    }
}
