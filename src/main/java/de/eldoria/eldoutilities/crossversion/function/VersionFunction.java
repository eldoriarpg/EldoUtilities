package de.eldoria.eldoutilities.crossversion.function;

import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.crossversion.UnsupportedVersionException;

import java.util.Map;
import java.util.function.Function;

public class VersionFunction<A, R> {
    private final Map<ServerVersion, Function<A, R>> functions;

    public VersionFunction(Map<ServerVersion, Function<A, R>> functions) {
        this.functions = functions;
    }

    public R apply(A a) {
        Function<A, R> function = functions.get(ServerVersion.CURRENT_VERSION);
        if (function == null) {
            throw new UnsupportedVersionException(ServerVersion.CURRENT_VERSION);
        }
        return function.apply(a);
    }
}
