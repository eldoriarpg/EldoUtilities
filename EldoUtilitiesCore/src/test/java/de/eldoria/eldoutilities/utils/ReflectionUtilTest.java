package de.eldoria.eldoutilities.utils;

import de.eldoria.eldoutilities.serialization.MapEntry;
import org.bukkit.configuration.serialization.SerializableAs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

public class ReflectionUtilTest {
    @Test
    public void testSerializationValueChange() {
        MapEntry entry = new MapEntry("name", null);
        SerializableAs serializable = entry.getClass().getAnnotation(SerializableAs.class);
        String oldName = serializable.value();

        try {
            ReflectionUtil.changeSerializedName(MapEntry.class, s -> s.replace("{plugin}", "dummyPlugin"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        SerializableAs annotation = MapEntry.class.getAnnotation(SerializableAs.class);
        String newName = annotation.value();
        Assertions.assertNotEquals(oldName, newName);
        Assertions.assertEquals("dummyPluginMapEntry", newName);
    }
}
