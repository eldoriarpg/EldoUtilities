package de.eldoria.eldoutilities.crossversion.functionbuilder;

import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.crossversion.function.TriVersionFunction;
import de.eldoria.eldoutilities.functions.TriFunction;

import java.util.EnumMap;
import java.util.Map;

public class TriFunctionBuilder<A, B, C, R> {
    private final Map<ServerVersion, TriFunction<A, B, C, R>> functions = new EnumMap<>(ServerVersion.class);

    public TriFunctionBuilder<A, B, C, R> addVersionFunction(TriFunction<A, B, C, R> function, ServerVersion... version) {
        for (ServerVersion serverVersion : version) {
            functions.put(serverVersion, function);
        }
        return this;
    }

    public TriFunctionBuilder<A, B, C, R> addVersionFunctionBetween(ServerVersion oldest, ServerVersion newest, TriFunction<A, B, C, R> function) {
        addVersionFunction(function, ServerVersion.versionsBetween(oldest, newest));
        return this;
    }

    public TriVersionFunction<A, B, C, R> build() {
        return new TriVersionFunction<>(functions);
    }
}
