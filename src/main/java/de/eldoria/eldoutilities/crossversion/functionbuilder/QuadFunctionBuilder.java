package de.eldoria.eldoutilities.crossversion.functionbuilder;

import de.eldoria.eldoutilities.crossversion.function.QuadVersionFunction;
import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.functions.QuadFunction;

import java.util.EnumMap;
import java.util.Map;

public class QuadFunctionBuilder<A, B, C, D, R> {
    private final Map<ServerVersion, QuadFunction<A, B, C, D, R>> functions = new EnumMap<>(ServerVersion.class);

    public QuadFunctionBuilder<A, B, C, D, R> addVersionFunction(QuadFunction<A, B, C, D, R> function, ServerVersion... version) {
        for (ServerVersion serverVersion : version) {
            functions.put(serverVersion, function);
        }
        return this;
    }

    public QuadFunctionBuilder<A, B, C, D, R> addVersionFunctionBetween(ServerVersion oldest, ServerVersion newest, QuadFunction<A, B, C, D, R> function) {
        addVersionFunction(function, ServerVersion.versionsBetween(oldest, newest));
        return this;
    }

    public QuadVersionFunction<A,B,C,D,R> build(){
        return new QuadVersionFunction<>(functions);
    }
}
