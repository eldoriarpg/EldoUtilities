package de.eldoria.eldoutilities.crossversion.function;

import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.crossversion.UnsupportedVersionException;
import de.eldoria.eldoutilities.functions.TriFunction;

import java.util.Map;

public class TriVersionFunction<A, B, C, R> {
    private final Map<ServerVersion, TriFunction<A, B, C, R>> functions;

    public TriVersionFunction(Map<ServerVersion, TriFunction<A, B, C, R>> functions) {
        this.functions = functions;
    }

    public R apply(A a, B b, C c) {
        TriFunction<A, B, C, R> function = functions.get(ServerVersion.CURRENT_VERSION);
        if(function == null){
            throw new UnsupportedVersionException(ServerVersion.CURRENT_VERSION);
        }
        return function.apply(a,b,c);
    }
}
