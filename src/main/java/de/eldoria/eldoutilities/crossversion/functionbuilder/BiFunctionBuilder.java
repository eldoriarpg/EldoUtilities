package de.eldoria.eldoutilities.crossversion.functionbuilder;

import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.crossversion.function.BiVersionFunction;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

public class BiFunctionBuilder<A, B, R> {
    private final Map<ServerVersion, BiFunction<A, B, R>> functions = new EnumMap<>(ServerVersion.class);

    public BiFunctionBuilder<A, B,  R> addVersionFunctionBetween(BiFunction<A, B, R> function, ServerVersion... version) {
        for (ServerVersion serverVersion : version) {
            functions.put(serverVersion, function);
        }
        return this;
    }

    public BiFunctionBuilder<A, B, R> addVersionFunctionBetween(ServerVersion oldest, ServerVersion newest, BiFunction<A, B, R> function) {
        addVersionFunctionBetween(function, ServerVersion.versionsBetween(oldest, newest));
        return this;
    }

    public BiVersionFunction<A,B,R> build(){
        return new BiVersionFunction<>(functions);
    }
}
