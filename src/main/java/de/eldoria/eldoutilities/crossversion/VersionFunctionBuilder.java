package de.eldoria.eldoutilities.crossversion;

import de.eldoria.eldoutilities.crossversion.functionbuilder.BiFunctionBuilder;
import de.eldoria.eldoutilities.crossversion.functionbuilder.FunctionBuilder;
import de.eldoria.eldoutilities.crossversion.functionbuilder.QuadFunctionBuilder;
import de.eldoria.eldoutilities.crossversion.functionbuilder.TriFunctionBuilder;

public interface VersionFunctionBuilder {
    public static <A, R> FunctionBuilder<A, R> functionBuilder(Class<A> a, Class<R> r) {
        return new FunctionBuilder<>();
    }

    public static <A, B, R> BiFunctionBuilder<A, B, R> biFunctionBuilder(Class<A> a, Class<B> b, Class<R> r) {
        return new BiFunctionBuilder<>();
    }

    public static <A, B, C, R> TriFunctionBuilder<A, B, C, R> triFunctionBuilder(Class<A> a, Class<B> b, Class<C> c, Class<R> r) {
        return new TriFunctionBuilder<>();
    }

    public static <A, B, C, D, R> QuadFunctionBuilder<A, B, C, D, R> quadFunctionBuilder(
            Class<A> a, Class<B> b, Class<C> c, Class<D> d, Class<R> r) {
        return new QuadFunctionBuilder<>();
    }
}
