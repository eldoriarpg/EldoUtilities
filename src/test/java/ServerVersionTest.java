import de.eldoria.eldoutilities.crossversion.ServerVersion;
import de.eldoria.eldoutilities.crossversion.functionbuilder.VersionFunctionBuilder;
import de.eldoria.eldoutilities.crossversion.function.VersionFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ServerVersionTest {
    @Test
    public void versionsBetweenTest() {
        System.out.println(Arrays.stream(ServerVersion.versionsBetween(ServerVersion.MC_1_9, ServerVersion.MC_1_16)).map(Enum::name).collect(Collectors.joining(", ")));
        Assertions.assertArrayEquals(
                new ServerVersion[] {ServerVersion.MC_1_12, ServerVersion.MC_1_13, ServerVersion.MC_1_14, ServerVersion.MC_1_15, ServerVersion.MC_1_16},
                ServerVersion.versionsBetween(ServerVersion.MC_1_9, ServerVersion.MC_1_16));
    }

    @Test
    public void functionBuilderTest() {
        VersionFunction<Boolean, Boolean> build = VersionFunctionBuilder.functionBuilder(Boolean.class, Boolean.class)
                .addVersionFunctionBetween(ServerVersion.MC_1_9, ServerVersion.MC_1_16,
                        b -> b)
                .addVersionFunction(b -> !b, ServerVersion.MC_1_8)
                .build();
        try {
            Field field = ServerVersion.CURRENT_VERSION.getClass().getField("CURRENT_VERSION");
            field.setAccessible(true);
            Field modifiers = field.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(ServerVersion.CURRENT_VERSION, ServerVersion.MC_1_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertFalse(build.apply(true));

        try {
            Field field = ServerVersion.CURRENT_VERSION.getClass().getField("CURRENT_VERSION");
            field.setAccessible(true);
            Field modifiers = field.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(ServerVersion.CURRENT_VERSION, ServerVersion.MC_1_9);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertTrue(build.apply(true));
    }
}
