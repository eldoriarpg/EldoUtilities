package de.eldoria.eldoutilities.crossversion.function;

import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.crossversion.UnsupportedVersionException;
import de.eldoria.eldoutilities.functions.QuadFunction;

import java.util.Map;

public class QuadVersionFunction<A, B, C, D, R> {
    private final Map<ServerVersion, QuadFunction<A, B, C, D, R>> functions;

    public QuadVersionFunction(Map<ServerVersion, QuadFunction<A, B, C, D, R>> functions) {
        this.functions = functions;
    }

    public R apply(A a, B b, C c, D d) {
        QuadFunction<A, B, C, D, R> function = functions.get(ServerVersion.CURRENT_VERSION);
        if(function == null){
            throw new UnsupportedVersionException(ServerVersion.CURRENT_VERSION);
        }
        return function.apply(a,b,c,d);
    }
}
