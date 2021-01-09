package de.eldoria.eldoutilities.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class DataContainerUtil {
    private DataContainerUtil() {
    }

    public static <T, Z> void setIfAbsent(@Nullable PersistentDataHolder holder, NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        if (holder == null) return;

        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (container.has(key, type)) return;

        container.set(key, type, value);
    }

    public static @Nullable <T, Z> Z compute(@Nullable PersistentDataHolder holder, NamespacedKey key, PersistentDataType<T, Z> type, Function<Z, Z> map) {
        if (holder == null) return null;

        PersistentDataContainer container = holder.getPersistentDataContainer();
        if (!container.has(key, type)) {
            container.set(key, type, map.apply(null));
            return container.get(key, type);
        }
        container.set(key, type, map.apply(container.get(key, type)));
        return container.get(key, type);
    }

    public static @Nullable <T, Z> Z compute(@Nullable ItemStack holder, NamespacedKey key, PersistentDataType<T, Z> type, Function<Z, Z> map) {
        if (holder == null) return null;

        ItemMeta itemMeta = holder.getItemMeta();
        Z compute = compute(itemMeta, key, type, map);
        holder.setItemMeta(itemMeta);
        return compute;
    }

    public static boolean byteToBoolean(Byte aByte) {
        if (aByte == null) return false;

        return aByte == (byte) 1;
    }

    public static byte booleanToByte(boolean aBoolean) {
        return (byte) (aBoolean ? 1 : 0);
    }
}
