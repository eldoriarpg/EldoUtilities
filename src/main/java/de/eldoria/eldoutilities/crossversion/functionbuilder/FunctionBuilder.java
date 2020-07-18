package de.eldoria.eldoutilities.crossversion.functionbuilder;

import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.crossversion.function.VersionFunction;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class FunctionBuilder<A, R> {
    private final Map<ServerVersion, Function<A, R>> functions = new EnumMap<>(ServerVersion.class);

    public FunctionBuilder<A, R> addVersionFunction(Function<A,  R> function, ServerVersion... version) {
        for (ServerVersion serverVersion : version) {
            functions.put(serverVersion, function);
        }
        return this;
    }

    public FunctionBuilder<A, R> addVersionFunctionBetween(ServerVersion newest, ServerVersion oldest, Function<A,R> function) {
        addVersionFunction(function, ServerVersion.versionsBetween(oldest, newest));
        return this;
    }

    public VersionFunction<A,R> build(){
        return new VersionFunction<>(functions);
    }
}
